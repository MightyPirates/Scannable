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
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public enum ScanningAPIImpl implements ScanningAPI {
    INSTANCE;

    // --------------------------------------------------------------------- //

    public static long computeScanGrowthDuration() {
        return Constants.SCAN_GROWTH_DURATION * Minecraft.getMinecraft().gameSettings.renderDistanceChunks / 12;
    }

    public static float computeRadius(final long start, final float adjustedDuration) {
        final float progress = (System.currentTimeMillis() - start) / adjustedDuration;
        return 16 + progress * Constants.SCAN_RADIUS;
    }

    // --------------------------------------------------------------------- //

    private final List<ScanResultProvider> providers = new ArrayList<>();

    // List for collecting results during an active scan.
    private final List<ScanResult> collectingResults = new ArrayList<>();

    // Results get copied from the collectingResults list in here when a scan
    // completes. This is to avoid clearing active results by *starting* a scan.
    private final List<ScanResult> pendingResults = new ArrayList<>();
    private final List<ScanResult> renderingResults = new ArrayList<>();

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
            provider.computeScanResults(collectingResults);
            if (finish) {
                provider.reset();
            }
        }

        if (finish) {
            clear();

            lastScanCenter = entity.getPositionVector();
            currentStart = System.currentTimeMillis();

            pendingResults.addAll(collectingResults);
            pendingResults.sort(Comparator.comparing(result -> -lastScanCenter.distanceTo(result.getPosition())));

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
                    renderingResults.remove(renderingResults.size() - 1);
                } else {
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
            final ScanResult result = pendingResults.get(pendingResults.size() - 1);
            final Vec3d position = result.getPosition();
            if (lastScanCenter.distanceTo(position) <= radius) {
                pendingResults.remove(pendingResults.size() - 1);
                result.initialize();
                synchronized (renderingResults) {
                    renderingResults.add(result);
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
        final Vec3d entityPos = new Vec3d(posX, posY, posZ);
        final Vec2f entityAngle = new Vec2f((float) entityYaw, (float) entityPitch);

        final ICamera frustum = new Frustum();
        frustum.setPosition(posX, posY, posZ);

        synchronized (renderingResults) {
            for (ScanResult scanResult : renderingResults) {
                final AxisAlignedBB bounds = scanResult.getRenderBounds();
                if (bounds == null || frustum.isBoundingBoxInFrustum(bounds)) {
                    scanResult.render(entity, entityPos, entityAngle, event.getPartialTicks());
                }
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
            renderingResults.forEach(ScanResult::dispose);
            renderingResults.clear();
        }

        lastScanCenter = null;
        currentStart = -1;
    }
}
