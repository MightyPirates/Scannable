package li.cil.scannable.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import li.cil.scannable.common.config.Strings;
import li.cil.scannable.common.container.EntityModuleContainerMenu;
import li.cil.scannable.common.item.ConfigurableEntityScannerModuleItem;
import li.cil.scannable.common.network.Network;
import li.cil.scannable.common.network.message.SetConfiguredModuleItemAtMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class ConfigurableEntityScannerModuleContainerScreen extends AbstractConfigurableScannerModuleContainerScreen<EntityModuleContainerMenu, EntityType<?>> {
    private static final Map<EntityType<?>, Entity> RENDER_ENTITIES = new HashMap<>();

    public ConfigurableEntityScannerModuleContainerScreen(final EntityModuleContainerMenu container, final Inventory inventory, final Component title) {
        super(container, inventory, title, Strings.GUI_ENTITIES_LIST_CAPTION);
    }

    // --------------------------------------------------------------------- //

    @Override
    protected List<EntityType<?>> getConfiguredItems(final ItemStack stack) {
        return ConfigurableEntityScannerModuleItem.getEntityTypes(stack);
    }

    @Override
    protected Component getItemName(final EntityType<?> entityType) {
        return entityType.getDescription();
    }

    @Override
    protected void renderConfiguredItem(final EntityType<?> entityType, final int x, final int y) {
        renderEntity(x + 8, y + 13, entityType);
    }

    @Override
    protected void configureItemAt(final ItemStack stack, final int slot, final ItemStack value) {
        if (value.getItem() instanceof SpawnEggItem) {
            final EntityType<?> entityType = ((SpawnEggItem) value.getItem()).getType(value.getTag());
            final ResourceLocation registryName = Registry.ENTITY_TYPE.getKey(entityType);
            if (registryName != null) {
                Network.sendToServer(new SetConfiguredModuleItemAtMessage(menu.containerId, slot, registryName));
            }
        }
    }

    private void renderEntity(final int x, final int y, final EntityType<?> entityType) {
        final Entity entity = getRenderEntity(entityType);
        if (entity == null) {
            return;
        }

        entity.level = menu.getPlayer().level;
        final EntityDimensions bounds = entityType.getDimensions();
        final float size = Math.max(bounds.width, bounds.height);
        final float scale = 11.0f / size;

        final PoseStack poseStack = new PoseStack();
        poseStack.translate(x, y, 0);
        poseStack.scale(scale, scale, scale);
        final Quaternion quaternion = Vector3f.ZP.rotationDegrees(180);
        quaternion.mul(Vector3f.XN.rotationDegrees(-20));
        quaternion.mul(Vector3f.YP.rotationDegrees(30));
        poseStack.mulPose(quaternion);

        final EntityRenderDispatcher renderManager = Minecraft.getInstance().getEntityRenderDispatcher();
        quaternion.conj();
        renderManager.overrideCameraOrientation(quaternion);
        renderManager.setRenderShadow(false);

        final MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        renderManager.render(entity, 0, 0, 0, 0, 1, poseStack, buffer, 0xf000f0);
        buffer.endBatch();

        renderManager.setRenderShadow(true);
    }

    @Nullable
    private Entity getRenderEntity(final EntityType<?> entityType) {
        return RENDER_ENTITIES.computeIfAbsent(entityType, t -> t.create(menu.getPlayer().level));
    }
}
