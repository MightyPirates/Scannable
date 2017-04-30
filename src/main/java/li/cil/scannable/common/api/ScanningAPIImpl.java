package li.cil.scannable.common.api;

import li.cil.scannable.api.detail.ScanningAPI;
import li.cil.scannable.api.scanning.ScanResult;
import li.cil.scannable.api.scanning.ScanResultProvider;
import li.cil.scannable.client.renderer.ScannerRenderer;
import li.cil.scannable.common.config.Constants;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
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

    private final List<ScanResultProvider> providers = new ArrayList<>();

    private long lastScan = -1;
    @Nullable
    private Vec3d lastScanCenter;

    private final List<ScanResult> pendingResults = new ArrayList<>();
    private final List<ScanResult> diegeticResults = new ArrayList<>();
    private final List<ScanResult> nonDiegeticResults = new ArrayList<>();

    // --------------------------------------------------------------------- //

    public void beginScan(final EntityPlayer player) {
        clear();

        lastScanCenter = player.getPositionVector();
        for (final ScanResultProvider provider : providers) {
            provider.initialize(player, lastScanCenter, Constants.SCAN_RADIUS, Constants.SCAN_COMPUTE_DURATION);
        }
    }

    public void updateScan(final boolean finish) {
        if (lastScanCenter == null) {
            throw new IllegalStateException("Called updateScan without calling beginScan first.");
        }
        if (lastScan >= 0) {
            throw new IllegalStateException("Called updateScan after scan was completed.");
        }

        for (final ScanResultProvider provider : providers) {
            provider.computeScanResults(pendingResults);
            if (finish) {
                provider.reset();
            }
        }

        if (finish) {
            pendingResults.sort(Comparator.comparing(result -> -lastScanCenter.distanceTo(result.getPosition())));

            ScannerRenderer.INSTANCE.ping(lastScanCenter);

            lastScan = System.currentTimeMillis();
        }
    }

    public void cancelScan() {
        clear();
    }

    @SubscribeEvent
    public void onClientTick(final TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        if (lastScanCenter == null || lastScan < 0) {
            return;
        }

        if (lastScan + Constants.SCAN_STAY_DURATION < System.currentTimeMillis()) {
            clear();
            return;
        }

        if (pendingResults.size() <= 0) {
            return;
        }

        final float progress = MathHelper.clamp((System.currentTimeMillis() - lastScan) / Constants.SCAN_GROWTH_DURATION, 0, 1);
        final float radius = progress * Constants.SCAN_RADIUS;

        while (pendingResults.size() > 0) {
            final ScanResult result = pendingResults.get(pendingResults.size() - 1);
            final Vec3d position = result.getPosition();
            if (lastScanCenter.distanceTo(position) <= radius) {
                pendingResults.remove(pendingResults.size() - 1);
                switch (result.getRenderType()) {
                    case DIEGETIC:
                        diegeticResults.add(result);
                        break;
                    case NON_DIEGETIC:
                        nonDiegeticResults.add(result);
                        break;
                }
            } else {
                break; // List is sorted, so nothing else is in range.
            }
        }
    }

    @SubscribeEvent
    public void onRenderLast(final RenderWorldLastEvent event) {
        for (ScanResult scanResult : diegeticResults) {
            scanResult.renderDiegetic(event.getPartialTicks());
        }
    }

    @SubscribeEvent
    public void onRenderOverlay(final RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        for (ScanResult scanResult : nonDiegeticResults) {
            scanResult.renderNonDiegetic(event.getPartialTicks());
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
        pendingResults.forEach(ScanResult::dispose);
        diegeticResults.forEach(ScanResult::dispose);
        nonDiegeticResults.forEach(ScanResult::dispose);

        pendingResults.clear();
        diegeticResults.clear();
        nonDiegeticResults.clear();

        lastScanCenter = null;
        lastScan = -1;
    }
}
