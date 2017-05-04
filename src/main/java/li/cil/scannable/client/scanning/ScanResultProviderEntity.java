package li.cil.scannable.client.scanning;

import li.cil.scannable.api.Icons;
import li.cil.scannable.api.prefab.AbstractScanResultProvider;
import li.cil.scannable.api.scanning.ScanResult;
import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.init.Items;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
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
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public final class ScanResultProviderEntity extends AbstractScanResultProvider {
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
    // ScanResultProvider

    @Override
    public int getEnergyCost(final EntityPlayer player, final ItemStack module) {
        if (Items.isModuleAnimal(module)) {
            return Constants.ENERGY_COST_MODULE_ANIMAL;
        }
        if (Items.isModuleMonster(module)) {
            return Constants.ENERGY_COST_MODULE_MONSTER;
        }

        throw new IllegalArgumentException(String.format("Module not supported by this provider: %s", module));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void initialize(final EntityPlayer player, final Collection<ItemStack> modules, final Vec3d center, final float radius, final int scanTicks) {
        super.initialize(player, modules, center, radius, scanTicks);

        scanAnimal = false;
        scanMonster = false;
        for (final ItemStack module : modules) {
            scanAnimal |= Items.isModuleAnimal(module);
            scanMonster |= Items.isModuleMonster(module);
        }

        // TODO Spread this query over multiple ticks (reimplement inner loop).
        final AxisAlignedBB bounds = new AxisAlignedBB(center.xCoord - radius, center.yCoord - radius, center.zCoord - radius,
                                                       center.xCoord + radius, center.yCoord + radius, center.zCoord + radius);
        entities = player.getEntityWorld().getEntitiesWithinAABB(EntityLiving.class, bounds, this::FilterEntities);
        entitiesPerTick = MathHelper.ceil(entities.size() / (float) scanTicks);
        currentEntity = 0;
    }

    @SideOnly(Side.CLIENT)
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

    @SideOnly(Side.CLIENT)
    @Override
    public void render(final Entity entity, final List<ScanResult> results, final float partialTicks) {
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

        final Minecraft mc = Minecraft.getMinecraft();
        final EntityPlayer player = mc.player;
        final FontRenderer fontRenderer = mc.fontRenderer;
        final int height = fontRenderer.FONT_HEIGHT + 5;

        // Order results by distance to center of screen (deviation from look
        // vector) so that labels we're looking at are in front of others.
        results.sort(Comparator.comparing(result -> {
            final ScanResultEntity resultEntity = (ScanResultEntity) result;
            final Vec3d entityEyes = resultEntity.entity.getPositionEyes(partialTicks);
            final Vec3d toResult = entityEyes.subtract(playerEyes);
            return lookVec.dotProduct(toResult.normalize());
        }));

        for (final ScanResult result : results) {
            final ScanResultEntity resultEntity = (ScanResultEntity) result;
            final Vec3d entityEyes = resultEntity.entity.getPositionEyes(partialTicks);
            final Vec3d toResult = entityEyes.subtract(playerEyes);
            final float distance = (float) toResult.lengthVector();
            final float lookDirDot = (float) lookVec.dotProduct(toResult.normalize());
            final float sqLookDirDot = lookDirDot * lookDirDot;
            final float sq2LookDirDot = sqLookDirDot * sqLookDirDot;
            final float focusScale = MathHelper.clamp(sq2LookDirDot * sq2LookDirDot + 0.005f, 0.5f, 1f);
            final float scale = distance * focusScale * 0.005f;

            GlStateManager.pushMatrix();
            GlStateManager.translate(entityEyes.xCoord, entityEyes.yCoord, entityEyes.zCoord);
            GlStateManager.translate(-posX, -posY, -posZ);
            GlStateManager.rotate(-entityYaw, 0, 1, 0);
            GlStateManager.rotate(entityPitch, 1, 0, 0);
            GlStateManager.scale(-scale, -scale, scale);

            if (lookDirDot > 0.999f) {
                final String name = resultEntity.entity.getName();
                final String text;
                if (player.isSneaking()) {
                    text = I18n.format(Constants.GUI_OVERLAY_ENTITY_DETAILS, name, MathHelper.ceil(distance));
                } else {
                    text = name;
                }

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

    @SideOnly(Side.CLIENT)
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
