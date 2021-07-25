package li.cil.scannable.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Matrix4f;
import li.cil.scannable.api.scanning.ScanResult;
import li.cil.scannable.api.scanning.ScanResultProvider;
import li.cil.scannable.api.scanning.ScannerModule;
import li.cil.scannable.client.renderer.ScannerRenderer;
import li.cil.scannable.common.capabilities.Capabilities;
import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.config.Settings;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;

import javax.annotation.Nullable;
import java.util.*;

@OnlyIn(Dist.CLIENT)
public final class ScanManager {
    private static float computeTargetRadius() {
        return Minecraft.getInstance().gameRenderer.getRenderDistance();
    }

    public static int computeScanGrowthDuration() {
        return Constants.SCAN_GROWTH_DURATION * Minecraft.getInstance().options.renderDistance / Constants.REFERENCE_RENDER_DISTANCE;
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
    private static final Set<ScanResultProvider> collectingProviders = new HashSet<>();
    // List for collecting results during an active scan.
    private static final Map<ScanResultProvider, List<ScanResult>> collectingResults = new HashMap<>();

    // Results get copied from the collectingResults list in here when a scan
    // completes. This is to avoid clearing active results by *starting* a scan.
    private static final Map<ScanResultProvider, List<ScanResult>> pendingResults = new HashMap<>();
    private static final Map<ScanResultProvider, List<ScanResult>> renderingResults = new HashMap<>();
    // Temporary, re-used list to collect visible results each frame.
    private static final List<ScanResult> renderingList = new ArrayList<>();

    private static int scanningTicks = -1;
    private static long currentStart = -1;
    @Nullable private static Vec3 lastScanCenter;

    private static PoseStack viewModelStack;
    private static Matrix4f projectionMatrix;

    // --------------------------------------------------------------------- //

    public static void beginScan(final Player player, final List<ItemStack> stacks) {
        cancelScan();

        float scanRadius = Settings.baseScanRadius;

        final List<ScannerModule> modules = new ArrayList<>();
        for (final ItemStack stack : stacks) {
            final LazyOptional<ScannerModule> module = stack.getCapability(Capabilities.SCANNER_MODULE_CAPABILITY);
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

        final Vec3 center = player.position();
        for (final ScanResultProvider provider : collectingProviders) {
            provider.initialize(player, stacks, center, scanRadius, Constants.SCAN_COMPUTE_DURATION);
        }
    }

    public static void updateScan(final Entity entity, final boolean finish) {
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
            provider.collectScanResults(entity.getCommandSenderWorld(), result -> collectingResults.computeIfAbsent(provider, p -> new ArrayList<>()).add(result));
            provider.reset();
        }

        clear();

        lastScanCenter = entity.position();
        currentStart = System.currentTimeMillis();

        pendingResults.putAll(collectingResults);
        pendingResults.values().forEach(list -> list.sort(Comparator.comparing(result -> -lastScanCenter.distanceTo(result.getPosition()))));

        ScannerRenderer.INSTANCE.ping(lastScanCenter);

        cancelScan();
    }

    public static void cancelScan() {
        collectingProviders.clear();
        collectingResults.clear();
        scanningTicks = 0;
    }

    public static void onClientTick(final TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        final BlockGetter world = Minecraft.getInstance().level;
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
                        for (int i = Mth.ceil(list.size() * 0.5f); i > 0; i--) {
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
                final Vec3 position = result.getPosition();
                if (lastScanCenter.distanceToSqr(position) <= sqRadius) {
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

    public static void onRenderLast(final RenderWorldLastEvent event) {
        synchronized (renderingResults) {
            if (renderingResults.isEmpty()) {
                return;
            }

            viewModelStack = new PoseStack();
            viewModelStack.last().pose().load(event.getMatrixStack().last().pose());
            projectionMatrix = event.getProjectionMatrix();
        }
    }

    public static void onPreRenderGameOverlay(final RenderGameOverlayEvent.Pre event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        synchronized (renderingResults) {
            if (renderingResults.isEmpty()) {
                return;
            }

            // Using shaders so we render as game overlay; restore matrices as used for world rendering.
            RenderSystem.backupProjectionMatrix();
            RenderSystem.setProjectionMatrix(projectionMatrix);
            RenderSystem.getModelViewStack().pushPose();
            RenderSystem.getModelViewStack().last().pose().setIdentity();
            RenderSystem.applyModelViewMatrix();

            render(event.getPartialTicks(), viewModelStack, RenderSystem.getProjectionMatrix());

            RenderSystem.getModelViewStack().popPose();
            RenderSystem.applyModelViewMatrix();
            RenderSystem.restoreProjectionMatrix();
        }
    }

    private static void render(final float partialTicks, final PoseStack matrixStack, final Matrix4f projectionMatrix) {
        final Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        final Vec3 pos = camera.getPosition();

        final Frustum frustum = new Frustum(matrixStack.last().pose(), projectionMatrix);
        frustum.prepare(pos.x(), pos.y(), pos.z());

        RenderSystem.disableDepthTest();
        RenderSystem.setShaderColor(1, 1, 1, 1);

        matrixStack.pushPose();
        matrixStack.translate(-pos.x, -pos.y, -pos.z);

        // We render all results in batches, grouped by their provider.
        // This allows providers to do more optimized rendering, in e.g.
        // setting up the render state once before rendering all visuals,
        // or even set up display lists or VBOs.
        final MultiBufferSource.BufferSource renderTypeBuffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        for (final Map.Entry<ScanResultProvider, List<ScanResult>> entry : renderingResults.entrySet()) {
            // Quick and dirty frustum culling.
            for (final ScanResult result : entry.getValue()) {
                final AABB bounds = result.getRenderBounds();
                if (bounds == null || frustum.isVisible(bounds)) {
                    renderingList.add(result);
                }
            }

            if (!renderingList.isEmpty()) {
                entry.getKey().render(renderTypeBuffer, matrixStack, camera, partialTicks, renderingList);
                renderingList.clear();
            }
        }
        renderTypeBuffer.endBatch();

        matrixStack.popPose();

        RenderSystem.enableDepthTest();
    }

    // --------------------------------------------------------------------- //

    private static void clear() {
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
