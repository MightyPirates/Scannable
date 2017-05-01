package li.cil.scannable.client.scanning;

import li.cil.scannable.api.Icons;
import li.cil.scannable.api.prefab.AbstractScanResultProvider;
import li.cil.scannable.api.scanning.ScanResult;
import li.cil.scannable.common.capabilities.CapabilityScanResultProvider;
import li.cil.scannable.common.init.Items;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public final class ScanResultProviderEntity extends AbstractScanResultProvider implements ICapabilityProvider {
    public static final ScanResultProviderEntity INSTANCE = new ScanResultProviderEntity();

    // --------------------------------------------------------------------- //

    private boolean scanAnimal;
    private boolean scanMonster;
    private List<EntityLivingBase> entities;
    private int entitiesPerTick;
    private int currentEntity;

    // --------------------------------------------------------------------- //

    private ScanResultProviderEntity() {
    }

    // --------------------------------------------------------------------- //
    // ICapabilityProvider

    @Override
    public boolean hasCapability(@Nonnull final Capability<?> capability, @Nullable final EnumFacing facing) {
        return capability == CapabilityScanResultProvider.SCAN_RESULT_PROVIDER_CAPABILITY;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> T getCapability(@Nonnull final Capability<T> capability, @Nullable final EnumFacing facing) {
        if (capability == CapabilityScanResultProvider.SCAN_RESULT_PROVIDER_CAPABILITY) {
            return (T) this;
        }
        return null;
    }

    // --------------------------------------------------------------------- //
    // ScanResultProvider

    @Override
    public void initialize(final EntityPlayer player, final Collection<ItemStack> modules, final Vec3d center, final float radius, final int scanTicks) {
        super.initialize(player, modules, center, radius, scanTicks);

        scanAnimal = false;
        scanMonster = false;
        for (final ItemStack module : modules) {
            scanAnimal |= module.getItem() == Items.moduleAnimal;
            scanMonster |= module.getItem() == Items.moduleMonster;
        }

        // TODO Spread this query over multiple ticks (reimplement inner loop).
        final AxisAlignedBB bounds = new AxisAlignedBB(center.xCoord - radius, center.yCoord - radius, center.zCoord - radius,
                                                       center.xCoord + radius, center.yCoord + radius, center.zCoord + radius);
        entities = player.getEntityWorld().getEntitiesWithinAABB(EntityLiving.class, bounds, this::FilterEntities);
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

    private <T extends Entity> boolean FilterEntities(final T entity) {
        if (scanAnimal) {
            if (entity instanceof EntityAnimal) {
                return true;
            }
            if (entity instanceof EntityBat) {
                return true;
            }
            if (entity instanceof EntitySquid) {
                return true;
            }
        }

        if (scanMonster) {
            if (entity instanceof EntityMob) {
                return true;
            }
            if (entity instanceof EntitySlime) {
                return true;
            }
            if (entity instanceof EntityGhast) {
                return true;
            }
            if (entity instanceof EntityDragon) {
                return true;
            }
            if (entity instanceof EntityGolem) {
                return true;
            }
        }

        return false;
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
