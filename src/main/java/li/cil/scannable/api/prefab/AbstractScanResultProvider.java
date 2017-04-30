package li.cil.scannable.api.prefab;

import li.cil.scannable.api.scanning.ScanResultProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;

public abstract class AbstractScanResultProvider implements ScanResultProvider {
    protected Vec3d center;
    protected float radius;

    @Override
    public void initialize(final EntityPlayer player, final Vec3d center, final float radius, final int scanTicks) {
        this.center = center;
        this.radius = radius;
    }
}
