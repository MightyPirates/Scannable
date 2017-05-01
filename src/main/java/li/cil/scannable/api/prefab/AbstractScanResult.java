package li.cil.scannable.api.prefab;

import li.cil.scannable.api.scanning.ScanResult;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;

public abstract class AbstractScanResult implements ScanResult {
    // --------------------------------------------------------------------- //
    // ScanResult

    @Nullable
    @Override
    public AxisAlignedBB getRenderBounds() {
        final Vec3d worldPos = getPosition();
        return new AxisAlignedBB(new BlockPos(worldPos));
    }
}
