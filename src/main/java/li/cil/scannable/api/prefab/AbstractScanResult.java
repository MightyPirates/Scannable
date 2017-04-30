package li.cil.scannable.api.prefab;

import li.cil.scannable.api.scanning.ScanResult;
import net.minecraft.util.math.Vec3d;

public abstract class AbstractScanResult implements ScanResult {
    @Override
    public void initialize() {
    }

    @Override
    public void dispose() {
    }

    @Override
    public void renderDiegetic(final float partialTicks) {
    }

    @Override
    public void renderNonDiegetic(final float partialTicks) {
        final Vec3d worldPos = getPosition();

    }
}
