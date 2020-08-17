package li.cil.scannable.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.container.EntityModuleContainer;
import li.cil.scannable.common.item.ItemScannerModuleEntityConfigurable;
import li.cil.scannable.common.network.Network;
import li.cil.scannable.common.network.message.MessageSetConfiguredModuleItemAt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityModuleScreen extends AbstractConfigurableModuleScreen<EntityModuleContainer, EntityType<?>> {
    private static final Map<EntityType<?>, Entity> RENDER_ENTITIES = new HashMap<>();

    public EntityModuleScreen(final EntityModuleContainer container, final PlayerInventory inventory, final ITextComponent title) {
        super(container, inventory, title, Constants.GUI_MODULE_ENTITY_LIST);
    }

    // --------------------------------------------------------------------- //

    @Override
    protected List<EntityType<?>> getConfiguredItems(final ItemStack stack) {
        return ItemScannerModuleEntityConfigurable.getEntityTypes(stack);
    }

    @Override
    protected ITextComponent getItemName(final EntityType<?> entityType) {
        return entityType.getName();
    }

    @Override
    protected void renderConfiguredItem(final EntityType<?> entityType, final int x, final int y) {
        renderEntity(x + 8, y + 13, entityType);
    }

    @Override
    protected void configureItemAt(final ItemStack stack, final int slot, final ItemStack value) {
        if (value.getItem() instanceof SpawnEggItem) {
            final EntityType<?> entityType = ((SpawnEggItem) value.getItem()).getType(value.getTag());
            if (entityType != null) {
                final ResourceLocation registryName = entityType.getRegistryName();
                if (registryName != null) {
                    Network.INSTANCE.sendToServer(new MessageSetConfiguredModuleItemAt(container.windowId, slot, registryName.toString()));
                }
            }
        }
    }

    private void renderEntity(final int x, final int y, final EntityType<?> entityType) {
        final Entity entity = getRenderEntity(entityType);
        if (entity == null) {
            return;
        }

        entity.setWorld(playerInventory.player.getEntityWorld());
        final EntitySize bounds = entityType.getSize();
        final float size = Math.max(bounds.width, bounds.height);
        final float scale = 11.0f / size;

        RenderSystem.pushMatrix();
        RenderSystem.translatef(x, y, 1050);
        RenderSystem.scalef(1, 1, -1);
        final MatrixStack matrixStack = new MatrixStack();
        matrixStack.translate(0, 0, 1000);
        matrixStack.scale(scale, scale, scale);
        final Quaternion quaternion = Vector3f.ZP.rotationDegrees(180);
        quaternion.multiply(Vector3f.XN.rotationDegrees(20));
        quaternion.multiply(Vector3f.YP.rotationDegrees(150));
        matrixStack.rotate(quaternion);

        final EntityRendererManager renderManager = Minecraft.getInstance().getRenderManager();
        quaternion.conjugate();
        renderManager.setCameraOrientation(quaternion);
        renderManager.setRenderShadow(false);

        final IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        renderManager.renderEntityStatic(entity, 0, 0, 0, 0, 1, matrixStack, buffer, 0xf000f0);
        buffer.finish();

        renderManager.setRenderShadow(true);

        RenderSystem.popMatrix();
    }

    @Nullable
    private Entity getRenderEntity(final EntityType<?> entityType) {
        return RENDER_ENTITIES.computeIfAbsent(entityType, t -> t.create(playerInventory.player.getEntityWorld()));
    }
}
