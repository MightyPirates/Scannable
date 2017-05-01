package li.cil.scannable.client.scanning;

import li.cil.scannable.api.Icons;
import li.cil.scannable.api.prefab.AbstractScanResult;
import li.cil.scannable.api.prefab.AbstractScanResultProvider;
import li.cil.scannable.api.scanning.ScanResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import java.util.Collection;
import java.util.List;

public final class ScanResultProviderEntity extends AbstractScanResultProvider {
    private Vec3d center;
    private float radius;
    private List<EntityLivingBase> entities;
    private int entitiesPerTick;
    private int currentEntity;

    // --------------------------------------------------------------------- //
    // ScanResultProvider

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

    // --------------------------------------------------------------------- //

    private static final class ScanResultEntity extends AbstractScanResult {
        private final Entity entity;

        ScanResultEntity(final Entity entity) {
            this.entity = entity;
        }

        // --------------------------------------------------------------------- //
        // ScanResult

        @Override
        public Vec3d getPosition() {
            return entity.getPositionVector();
        }

        // --------------------------------------------------------------------- //
        // AbstractScanResult

        @Override
        protected Vec3d getPosition(final float partialTicks) {
            return entity.getPositionEyes(partialTicks);
        }

        @Override
        protected void render2D(final Entity player, final Vec3d playerPos, final Vec2f playerAngle, final float partialTicks) {
            final Vec3d lookVec = player.getLook(partialTicks).normalize();
            final Vec3d toEntity = getPosition(partialTicks).subtract(player.getPositionEyes(partialTicks)).normalize();
            if (lookVec.dotProduct(toEntity) > 0.999f) {
                final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
                final String name = entity.getName();
                final int width = fontRenderer.getStringWidth(name) + 16;
                final int height = fontRenderer.FONT_HEIGHT + 4;

                GlStateManager.enableBlend();
                GlStateManager.disableTexture2D();
                GlStateManager.pushMatrix();
                GlStateManager.translate(width / 2, 0, 0);
                GlStateManager.color(0, 0, 0, 0.6f);
                renderQuad(width, height);
                GlStateManager.popMatrix();
                GlStateManager.enableTexture2D();
                GlStateManager.disableBlend();

                fontRenderer.drawString(name, 12, -4, 0xFFFFFFFF, true);
            }

            if (entity instanceof EntityMob) {
                Minecraft.getMinecraft().getTextureManager().bindTexture(Icons.WARNING);
            } else {
                Minecraft.getMinecraft().getTextureManager().bindTexture(Icons.INFO);
            }

            GlStateManager.color(1, 1, 1, 1);
            renderQuad(16, 16);
        }
    }
}
