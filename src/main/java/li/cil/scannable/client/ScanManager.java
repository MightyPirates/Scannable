package li.cil.scannable.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import li.cil.scannable.api.scanning.ScanResult;
import li.cil.scannable.api.scanning.ScanResultProvider;
import li.cil.scannable.api.scanning.ScannerModule;
import li.cil.scannable.client.renderer.ScannerRenderer;
import li.cil.scannable.common.capabilities.CapabilityScannerModule;
import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.config.Settings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.*;

@OnlyIn(Dist.CLIENT)
public enum ScanManager {
    INSTANCE;

    // --------------------------------------------------------------------- //

    private static float computeTargetRadius() {
        return Minecraft.getInstance().gameRenderer.getFarPlaneDistance();
    }

    public static int computeScanGrowthDuration() {
        return Constants.SCAN_GROWTH_DURATION * Minecraft.getInstance().gameSettings.renderDistanceChunks / Constants.REFERENCE_RENDER_DISTANCE;
    }

    public static float computeRadius(final long start, final float duration) {
        // Scan wave speeds up exponentially. To avoid the initial speed being
        // near zero due to that we offset the time and adjust the remaining
        // parameters accordingly. Base equation is:
        //   r = a + (t + b)^2 * c
        // with r := 0 and target radius and t := 0 and target time this yields:
        //   c = r1/((t1 + b)^2 - b*b)
        //   a = -r1*b*b/((t1 + b)^2 - b*b)

        final float r1 = computeTargetRadius();
        final float t1 = duration;
        final float b = Constants.SCAN_TIME_OFFSET;
        final float n = 1f / ((t1 + b) * (t1 + b) - b * b);
        final float a = -r1 * b * b * n;
        final float c = r1 * n;

        final float t = (float) (System.currentTimeMillis() - start);

        return Constants.SCAN_INITIAL_RADIUS + a + (t + b) * (t + b) * c;
    }

    // --------------------------------------------------------------------- //

    // List of providers currently used to scan.
    private final Set<ScanResultProvider> collectingProviders = new HashSet<>();
    // List for collecting results during an active scan.
    private final Map<ScanResultProvider, List<ScanResult>> collectingResults = new HashMap<>();

    // Results get copied from the collectingResults list in here when a scan
    // completes. This is to avoid clearing active results by *starting* a scan.
    private final Map<ScanResultProvider, List<ScanResult>> pendingResults = new HashMap<>();
    private final Map<ScanResultProvider, List<ScanResult>> renderingResults = new HashMap<>();
    // Temporary, re-used list to collect visible results each frame.
    private final List<ScanResult> renderingList = new ArrayList<>();

    private int scanningTicks = -1;
    private long currentStart = -1;
    @Nullable
    private Vector3d lastScanCenter;

    private MatrixStack viewMatrix;
    private Matrix4f projectionMatrix;

    // --------------------------------------------------------------------- //

    public void beginScan(final PlayerEntity player, final List<ItemStack> stacks) {
        cancelScan();

        float scanRadius = Settings.baseScanRadius;

        final List<ScannerModule> modules = new ArrayList<>();
        for (final ItemStack stack : stacks) {
            final LazyOptional<ScannerModule> module = stack.getCapability(CapabilityScannerModule.SCANNER_MODULE_CAPABILITY);
            module.ifPresent(modules::add);
        }
        for (final ScannerModule module : modules) {
            final ScanResultProvider provider = module.getResultProvider();
            if (provider != null) {
                collectingProviders.add(provider);
            }

            scanRadius = module.adjustGlobalRange(scanRadius);
        }

        if (collectingProviders.isEmpty()) {
            return;
        }

        final Vector3d center = player.getPositionVec();
        for (final ScanResultProvider provider : collectingProviders) {
            provider.initialize(player, stacks, center, scanRadius, Constants.SCAN_COMPUTE_DURATION);
        }
    }

    public void updateScan(final Entity entity, final boolean finish) {
        final int remaining = Constants.SCAN_COMPUTE_DURATION - scanningTicks;

        if (!finish) {
            if (remaining <= 0) {
                return;
            }

            for (final ScanResultProvider provider : collectingProviders) {
                provider.computeScanResults();
            }

            ++scanningTicks;

            return;
        }

        for (int i = 0; i < remaining; i++) {
            for (final ScanResultProvider provider : collectingProviders) {
                provider.computeScanResults();
            }
        }

        for (final ScanResultProvider provider : collectingProviders) {
            provider.collectScanResults(entity.getEntityWorld(), result -> collectingResults.computeIfAbsent(provider, p -> new ArrayList<>()).add(result));
            provider.reset();
        }

        clear();

        lastScanCenter = entity.getPositionVec();
        currentStart = System.currentTimeMillis();

        pendingResults.putAll(collectingResults);
        pendingResults.values().forEach(list -> list.sort(Comparator.comparing(result -> -lastScanCenter.distanceTo(result.getPosition()))));

        ScannerRenderer.INSTANCE.ping(lastScanCenter);

        cancelScan();
    }

    public void cancelScan() {
        collectingProviders.clear();
        collectingResults.clear();
        scanningTicks = 0;
    }

    @SubscribeEvent
    public void onClientTick(final TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        final IBlockReader world = Minecraft.getInstance().world;
        if (world == null) {
            return;
        }

        if (lastScanCenter == null || currentStart < 0) {
            return;
        }

        if (Settings.scanStayDuration < (int) (System.currentTimeMillis() - currentStart)) {
            pendingResults.forEach((provider, results) -> results.forEach(ScanResult::close));
            pendingResults.clear();
            synchronized (renderingResults) {
                if (!renderingResults.isEmpty()) {
                    for (final Iterator<Map.Entry<ScanResultProvider, List<ScanResult>>> iterator = renderingResults.entrySet().iterator(); iterator.hasNext(); ) {
                        final Map.Entry<ScanResultProvider, List<ScanResult>> entry = iterator.next();
                        final List<ScanResult> list = entry.getValue();
                        for (int i = MathHelper.ceil(list.size() / 2f); i > 0; i--) {
                            list.get(list.size() - 1).close();
                            list.remove(list.size() - 1);
                        }
                        if (list.isEmpty()) {
                            iterator.remove();
                        }
                    }
                }

                if (renderingResults.isEmpty()) {
                    clear();
                }
            }
            return;
        }

        if (pendingResults.size() <= 0) {
            return;
        }

        final float radius = computeRadius(currentStart, computeScanGrowthDuration());
        final float sqRadius = radius * radius;

        final Iterator<Map.Entry<ScanResultProvider, List<ScanResult>>> iterator = pendingResults.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<ScanResultProvider, List<ScanResult>> entry = iterator.next();
            final ScanResultProvider provider = entry.getKey();
            final List<ScanResult> results = entry.getValue();

            while (results.size() > 0) {
                final ScanResult result = results.get(results.size() - 1);
                final Vector3d position = result.getPosition();
                if (lastScanCenter.squareDistanceTo(position) <= sqRadius) {
                    results.remove(results.size() - 1);
                    synchronized (renderingResults) {
                        renderingResults.computeIfAbsent(provider, p -> new ArrayList<>()).add(result);
                    }
                } else {
                    break; // List is sorted, so nothing else is in range.
                }
            }

            if (results.size() == 0) {
                iterator.remove();
            }
        }
    }

    @SubscribeEvent
    public void onRenderLast(final RenderWorldLastEvent event) {
        synchronized (renderingResults) {
            if (renderingResults.isEmpty()) {
                return;
            }

            viewMatrix = new MatrixStack();
            viewMatrix.getLast().getMatrix().set(event.getMatrixStack().getLast().getMatrix());
            projectionMatrix = event.getProjectionMatrix();
        }
    }

    @SubscribeEvent
    public void onPreRenderGameOverlay(final RenderGameOverlayEvent.Pre event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        synchronized (renderingResults) {
            if (renderingResults.isEmpty()) {
                return;
            }

            // Using shaders so we render as game overlay; restore matrices as used for world rendering.
            RenderSystem.matrixMode(GL11.GL_PROJECTION);
            RenderSystem.pushMatrix();
            RenderSystem.loadIdentity();
            RenderSystem.multMatrix(projectionMatrix);
            RenderSystem.matrixMode(GL11.GL_MODELVIEW);
            RenderSystem.pushMatrix();
            RenderSystem.loadIdentity();

            render(event.getPartialTicks(), viewMatrix, projectionMatrix);

            RenderSystem.matrixMode(GL11.GL_PROJECTION);
            RenderSystem.popMatrix();
            RenderSystem.matrixMode(GL11.GL_MODELVIEW);
            RenderSystem.popMatrix();
        }
    }

    private void render(final float partialTicks, final MatrixStack matrixStack, final Matrix4f projectionMatrix) {
        final ActiveRenderInfo activeRenderInfo = Minecraft.getInstance().gameRenderer.getActiveRenderInfo();
        final Vector3d pos = activeRenderInfo.getProjectedView();

        final ClippingHelper frustum = new ClippingHelper(matrixStack.getLast().getMatrix(), projectionMatrix);
        frustum.setCameraPosition(pos.getX(), pos.getY(), pos.getZ());

        RenderSystem.disableDepthTest();

        matrixStack.push();
        matrixStack.translate(-pos.x, -pos.y, -pos.z);

        // We render all results in batches, grouped by their provider.
        // This allows providers to do more optimized rendering, in e.g.
        // setting up the render state once before rendering all visuals,
        // or even set up display lists or VBOs.
        final IRenderTypeBuffer.Impl renderTypeBuffer = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
        for (final Map.Entry<ScanResultProvider, List<ScanResult>> entry : renderingResults.entrySet()) {
            // Quick and dirty frustum culling.
            for (final ScanResult result : entry.getValue()) {
                final AxisAlignedBB bounds = result.getRenderBounds();
                if (bounds == null || frustum.isBoundingBoxInFrustum(bounds)) {
                    renderingList.add(result);
                }
            }

            if (!renderingList.isEmpty()) {
                entry.getKey().render(renderTypeBuffer, matrixStack, projectionMatrix, activeRenderInfo, partialTicks, renderingList);
                renderingList.clear();
            }
        }
        renderTypeBuffer.finish();

        matrixStack.pop();

        RenderSystem.enableDepthTest();
    }

    // --------------------------------------------------------------------- //

    private void clear() {
        pendingResults.clear();

        synchronized (renderingResults) {
            renderingResults.forEach((provider, results) -> {
                provider.reset();
                results.forEach(ScanResult::close);
            });
            renderingResults.clear();
        }

        lastScanCenter = null;
        currentStart = -1;
    }
}
