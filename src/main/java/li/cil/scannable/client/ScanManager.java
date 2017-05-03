package li.cil.scannable.client;

import li.cil.scannable.api.scanning.ScanResult;
import li.cil.scannable.api.scanning.ScanResultProvider;
import li.cil.scannable.client.renderer.ScannerRenderer;
import li.cil.scannable.common.capabilities.CapabilityScanResultProvider;
import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.init.Items;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SideOnly(Side.CLIENT)
public enum ScanManager {
    INSTANCE;

    // --------------------------------------------------------------------- //

    // --------------------------------------------------------------------- //

    private static int computeTargetRadius() {
        return Minecraft.getMinecraft().gameSettings.renderDistanceChunks * Constants.CHUNK_SIZE - Constants.SCAN_INITIAL_RADIUS;
    }

    public static int computeScanGrowthDuration() {
        return Constants.SCAN_GROWTH_DURATION * Minecraft.getMinecraft().gameSettings.renderDistanceChunks / Constants.REFERENCE_RENDER_DISTANCE;
    }

    public static float computeRadius(final long start, final float duration) {
        // Scan wave speeds up exponentially. To avoid the initial speed being
        // near zero due to that we offset the time and adjust the remaining
        // parameters accordingly. Base equation is:
        //   r = a + (t + b)^2 * c
        // with r := 0 and target radius and t := 0 and target time this yields:
        //   c = r1/((t1 + b)^2 - b*b)
        //   a = -r1*b*b/((t1 + b)^2 - b*b)

        final float r1 = (float) computeTargetRadius();
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
    private final List<ScanResultWithProvider> collectingResults = new ArrayList<>();

    // Results get copied from the collectingResults list in here when a scan
    // completes. This is to avoid clearing active results by *starting* a scan.
    private final List<ScanResultWithProvider> pendingResults = new ArrayList<>();
    private final Map<ScanResultProvider, List<ScanResult>> renderingResults = new HashMap<>();
    // Temporary, re-used list to collect visible results each frame.
    private final List<ScanResult> renderingList = new ArrayList<>();

    private long currentStart = -1;
    @Nullable
    private Vec3d lastScanCenter;

    // --------------------------------------------------------------------- //

    public void beginScan(final EntityPlayer player, final List<ItemStack> modules) {
        cancelScan();

        float scanRadius = Constants.SCAN_RADIUS;

        for (final ItemStack module : modules) {
            final ScanResultProvider provider = module.getCapability(CapabilityScanResultProvider.SCAN_RESULT_PROVIDER_CAPABILITY, null);
            if (provider != null) {
                collectingProviders.add(provider);
            }

            if (module.getItem() == Items.moduleRange) {
                scanRadius += Constants.MODULE_RANGE_RADIUS_INCREASE;
            }
        }

        if (collectingProviders.isEmpty()) {
            return;
        }

        final Vec3d center = player.getPositionVector();
        for (final ScanResultProvider provider : collectingProviders) {
            provider.initialize(player, modules, center, scanRadius, Constants.SCAN_COMPUTE_DURATION);
        }
    }

    public void updateScan(final Entity entity, final boolean finish) {
        for (final ScanResultProvider provider : collectingProviders) {
            provider.computeScanResults(result -> collectingResults.add(new ScanResultWithProvider(provider, result)));
            if (finish) {
                provider.reset();
            }
        }

        if (finish) {
            clear();

            lastScanCenter = entity.getPositionVector();
            currentStart = System.currentTimeMillis();

            pendingResults.addAll(collectingResults);
            pendingResults.sort(Comparator.comparing(entry -> -lastScanCenter.distanceTo(entry.result.getPosition())));

            ScannerRenderer.INSTANCE.ping(lastScanCenter);

            cancelScan();
        }
    }

    public void cancelScan() {
        collectingProviders.clear();
        collectingResults.clear();
    }

    @SubscribeEvent
    public void onClientTick(final TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        if (lastScanCenter == null || currentStart < 0) {
            return;
        }

        if (Constants.SCAN_STAY_DURATION < (int) (System.currentTimeMillis() - currentStart)) {
            pendingResults.clear();
            synchronized (renderingResults) {
                if (!renderingResults.isEmpty()) {
                    for (Iterator<Map.Entry<ScanResultProvider, List<ScanResult>>> iterator = renderingResults.entrySet().iterator(); iterator.hasNext(); ) {
                        final Map.Entry<ScanResultProvider, List<ScanResult>> entry = iterator.next();
                        final List<ScanResult> list = entry.getValue();
                        for (int i = MathHelper.ceil(list.size() / 2f); i > 0; i--) {
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

        while (pendingResults.size() > 0) {
            final ScanResultWithProvider entry = pendingResults.get(pendingResults.size() - 1);
            final Vec3d position = entry.result.getPosition();
            if (lastScanCenter.distanceTo(position) <= radius) {
                pendingResults.remove(pendingResults.size() - 1);
                if (!entry.provider.isValid(entry.result)) {
                    continue;
                }
                synchronized (renderingResults) {
                    final List<ScanResult> results = renderingResults.computeIfAbsent(entry.provider, provider -> new ArrayList<>());
                    results.add(entry.result);
                }
            } else {
                break; // List is sorted, so nothing else is in range.
            }
        }
    }

    @SubscribeEvent
    public void onRenderLast(final RenderWorldLastEvent event) {
        final Minecraft mc = Minecraft.getMinecraft();

        final Entity entity = mc.getRenderViewEntity();
        if (entity == null) {
            return;
        }

        final ICamera frustum = new Frustum();
        final double posX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * event.getPartialTicks();
        final double posY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * event.getPartialTicks();
        final double posZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * event.getPartialTicks();
        frustum.setPosition(posX, posY, posZ);

        GlStateManager.bindTexture(0);
        GlStateManager.color(1, 1, 1, 1);

        synchronized (renderingResults) {
            // We render all results in batches, grouped by their provider.
            // This allows providers to do more optimized rendering, in e.g.
            // setting up the render state once before rendering all visuals,
            // or even set up display lists or VBOs.
            for (final Map.Entry<ScanResultProvider, List<ScanResult>> entry : renderingResults.entrySet()) {
                // Quick and dirty frustum culling.
                for (ScanResult result : entry.getValue()) {
                    final AxisAlignedBB bounds = result.getRenderBounds();
                    if (bounds == null || frustum.isBoundingBoxInFrustum(bounds)) {
                        renderingList.add(result);
                    }
                }

                entry.getKey().render(entity, renderingList, event.getPartialTicks());

                renderingList.clear();
            }
        }
    }

    // --------------------------------------------------------------------- //

    private void clear() {
        pendingResults.clear();

        synchronized (renderingResults) {
            renderingResults.forEach((provider, results) -> provider.reset());
            renderingResults.clear();
        }

        lastScanCenter = null;
        currentStart = -1;
    }

    private static final class ScanResultWithProvider {
        final ScanResultProvider provider;
        final ScanResult result;

        private ScanResultWithProvider(final ScanResultProvider provider, final ScanResult result) {
            this.provider = provider;
            this.result = result;
        }
    }

}
