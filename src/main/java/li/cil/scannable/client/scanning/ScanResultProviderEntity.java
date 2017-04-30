package li.cil.scannable.client.scanning;

import li.cil.scannable.api.prefab.AbstractScanResult;
import li.cil.scannable.api.prefab.AbstractScanResultProvider;
import li.cil.scannable.api.scanning.ScanResult;
import li.cil.scannable.api.scanning.ScanResultRenderType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Collection;
import java.util.List;

public final class ScanResultProviderEntity extends AbstractScanResultProvider {
    private Vec3d center;
    private float radius;
    private List<EntityLivingBase> entities;
    private int entitiesPerTick;
    private int currentEntity;

    @Override
    public void initialize(final EntityPlayer player, final Vec3d center, final float radius, final int scanTicks) {
        this.center = center;
        this.radius = radius;
        final AxisAlignedBB bounds = new AxisAlignedBB(center.xCoord - radius, center.yCoord - radius, center.zCoord - radius,
                                                       center.xCoord + radius, center.yCoord + radius, center.zCoord + radius);
        entities = player.getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, bounds);
        entitiesPerTick = MathHelper.ceil(entities.size() / (float) scanTicks);
        currentEntity = 0;
    }

    @Override
    public void computeScanResults(final Collection<ScanResult> results) {
        final int end = Math.min(entities.size(), currentEntity + entitiesPerTick);
        for (; currentEntity < end; currentEntity++) {
            final Entity entity = entities.get(currentEntity);
            if (entity.isDead) {
                continue;
            }

            final Vec3d position = entity.getPositionVector();
            if (center.distanceTo(position) < radius) {
                results.add(new ScanResultEntity(entity));
            }
        }
    }

    @Override
    public void reset() {
        entities = null;
    }

    private static final class ScanResultEntity extends AbstractScanResult {
        private final Entity entity;

        ScanResultEntity(final Entity entity) {
            this.entity = entity;
        }

        @Override
        public Vec3d getPosition() {
            return entity.getPositionVector();
        }

        @Override
        public ScanResultRenderType getRenderType() {
            return ScanResultRenderType.NON_DIEGETIC;
        }
    }
}
