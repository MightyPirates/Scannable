package li.cil.scannable.client.scanning;

import li.cil.scannable.api.Icons;
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
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.function.Consumer;

public final class ScanResultProviderEntity extends AbstractScanResultProvider {
    private List<EntityLivingBase> entities;
    private int entitiesPerTick;
    private int currentEntity;

    // --------------------------------------------------------------------- //
    // ScanResultProvider

    @Override
    public void initialize(final EntityPlayer player, final Vec3d center, final float radius, final int scanTicks) {
        super.initialize(player, center, radius, scanTicks);
        final AxisAlignedBB bounds = new AxisAlignedBB(center.xCoord - radius, center.yCoord - radius, center.zCoord - radius,
                                                       center.xCoord + radius, center.yCoord + radius, center.zCoord + radius);
        // TODO Spread this query over multiple ticks (reimplement inner loop).
        entities = player.getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, bounds);
        entitiesPerTick = MathHelper.ceil(entities.size() / (float) scanTicks);
        currentEntity = 0;
    }

    @Override
    public void computeScanResults(final Consumer<ScanResult> callback) {
        final int end = Math.min(entities.size(), currentEntity + entitiesPerTick);
        for (; currentEntity < end; currentEntity++) {
            final Entity entity = entities.get(currentEntity);
            if (entity.isDead) {
                continue;
            }

            final Vec3d position = entity.getPositionVector();
            if (center.distanceTo(position) < radius) {
                callback.accept(new ScanResultEntity(entity));
            }
        }
    }

    @Override
    public void render(final Entity entity, final Iterable<ScanResult> results, final float partialTicks) {
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();

        final double posX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
        final double posY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
        final double posZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
        final float entityYaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks;
        final float entityPitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;

        final Vec3d lookVec = entity.getLook(partialTicks).normalize();
        final Vec3d playerEyes = entity.getPositionEyes(partialTicks);

        final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        final int height = fontRenderer.FONT_HEIGHT + 5;

        for (final ScanResult result : results) {
            final ScanResultEntity resultEntity = (ScanResultEntity) result;
            final Vec3d entityEyes = resultEntity.entity.getPositionEyes(partialTicks);
            final Vec3d toEntity = entityEyes.subtract(playerEyes);
            final float scale = (float) toEntity.lengthVector() * 0.005f;

            GlStateManager.pushMatrix();
            GlStateManager.translate(entityEyes.xCoord, entityEyes.yCoord, entityEyes.zCoord);
            GlStateManager.translate(-posX, -posY, -posZ);
            GlStateManager.rotate(-entityYaw, 0, 1, 0);
            GlStateManager.rotate(entityPitch, 1, 0, 0);
            GlStateManager.scale(-scale, -scale, scale);

            if (lookVec.dotProduct(toEntity.normalize()) > 0.999f) {
                final String text = resultEntity.entity.getName();
                final int width = fontRenderer.getStringWidth(text) + 16;

                GlStateManager.disableTexture2D();
                GlStateManager.pushMatrix();
                GlStateManager.translate(width / 2, 0, 0);

                GlStateManager.color(0, 0, 0, 0.6f);
                renderQuad(width, height);

                GlStateManager.popMatrix();
                GlStateManager.enableTexture2D();

                fontRenderer.drawString(text, 12, -4, 0xFFFFFFFF, true);
            }

            Minecraft.getMinecraft().getTextureManager().bindTexture(resultEntity.entity instanceof EntityMob ? Icons.WARNING : Icons.INFO);

            GlStateManager.color(1, 1, 1, 1);
            renderQuad(16, 16);

            GlStateManager.popMatrix();
        }

        GlStateManager.disableBlend();
        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
    }

    @Override
    public void reset() {
        super.reset();
        entities = null;
    }

    // --------------------------------------------------------------------- //

    private static final class ScanResultEntity implements ScanResult {
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

        @Override
        public AxisAlignedBB getRenderBounds() {
            return entity.getRenderBoundingBox();
        }
    }
}
