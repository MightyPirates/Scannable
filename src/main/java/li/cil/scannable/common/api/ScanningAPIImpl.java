package li.cil.scannable.common.api;

import li.cil.scannable.api.detail.ScanningAPI;
import li.cil.scannable.api.scanning.ScanResult;
import li.cil.scannable.api.scanning.ScanResultProvider;
import li.cil.scannable.client.renderer.ScannerRenderer;
import li.cil.scannable.common.config.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public enum ScanningAPIImpl implements ScanningAPI {
    INSTANCE;

    // --------------------------------------------------------------------- //

    public static long computeScanGrowthDuration() {
        return Constants.SCAN_GROWTH_DURATION * Minecraft.getMinecraft().gameSettings.renderDistanceChunks / 12;
    }

    public static float computeRadius(final long start, final float adjustedDuration) {
        final float progress = (System.currentTimeMillis() - start) / adjustedDuration;
        return 16 + progress * (Minecraft.getMinecraft().gameSettings.renderDistanceChunks - 1) * 16;
    }

    // --------------------------------------------------------------------- //

    private final List<ScanResultProvider> providers = new ArrayList<>();

    // List for collecting results during an active scan.
    private final List<ScanResultWithProvider> collectingResults = new ArrayList<>();

    // Results get copied from the collectingResults list in here when a scan
    // completes. This is to avoid clearing active results by *starting* a scan.
    private final List<ScanResultWithProvider> pendingResults = new ArrayList<>();
    private final Map<ScanResultProvider, List<ScanResult>> renderingResults = new HashMap<>();
    private final List<ScanResult> renderingList = new ArrayList<>();

    private long currentStart = -1;
    @Nullable
    private Vec3d lastScanCenter;

    // --------------------------------------------------------------------- //

    public void beginScan(final EntityPlayer player) {
        cancelScan();

        final Vec3d center = player.getPositionVector();
        for (final ScanResultProvider provider : providers) {
            provider.initialize(player, center, Constants.SCAN_RADIUS, Constants.SCAN_COMPUTE_DURATION);
        }
    }

    public void updateScan(final Entity entity, final boolean finish) {
        for (final ScanResultProvider provider : providers) {
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

        if (currentStart + Constants.SCAN_STAY_DURATION < System.currentTimeMillis()) {
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

        final long adjustedDuration = computeScanGrowthDuration();
        final float radius = computeRadius(currentStart, adjustedDuration);

        while (pendingResults.size() > 0) {
            final ScanResultWithProvider entry = pendingResults.get(pendingResults.size() - 1);
            final Vec3d position = entry.result.getPosition();
            if (lastScanCenter.distanceTo(position) <= radius) {
                pendingResults.remove(pendingResults.size() - 1);
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

        final double posX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * event.getPartialTicks();
        final double posY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * event.getPartialTicks();
        final double posZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * event.getPartialTicks();
        final double entityYaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * event.getPartialTicks();
        final double entityPitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * event.getPartialTicks();

        final ICamera frustum = new Frustum();
        frustum.setPosition(posX, posY, posZ);

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
    // ScanningAPI

    @Override
    public void addScanResultProvider(final ScanResultProvider provider) {
        providers.add(provider);
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
