package li.cil.scannable.client.gui;

import li.cil.scannable.common.config.Strings;
import li.cil.scannable.common.container.EntityModuleContainerMenu;
import li.cil.scannable.common.item.ConfigurableEntityScannerModuleItem;
import li.cil.scannable.common.network.Network;
import li.cil.scannable.common.network.message.SetConfiguredModuleItemAtMessage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static li.cil.scannable.util.UnitConversion.toRadians;

@Environment(EnvType.CLIENT)
public class ConfigurableEntityScannerModuleContainerScreen extends AbstractConfigurableScannerModuleContainerScreen<EntityModuleContainerMenu, EntityType<?>> {
    private static final Map<EntityType<?>, Entity> RENDER_ENTITIES = new HashMap<>();

    private static final float PREVIEW_TILT = -20;
    private static final float PREVIEW_YAW = -30;

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
    protected void renderConfiguredItem(final GuiGraphics graphics, final EntityType<?> entityType, final int x, final int y) {
        renderEntity(graphics, x, y, entityType);
    }

    @Override
    protected void configureItemAt(final ItemStack stack, final int slot, final ItemStack value) {
        if (value.getItem() instanceof SpawnEggItem) {
            final EntityType<?> entityType = ((SpawnEggItem) value.getItem()).getType(value);
            BuiltInRegistries.ENTITY_TYPE.getResourceKey(entityType).ifPresent(entityTypeResourceKey ->
                    Network.sendToServer(new SetConfiguredModuleItemAtMessage(menu.containerId, slot, entityTypeResourceKey.identifier())));
        }
    }

    private void renderEntity(final GuiGraphics graphics, final int x, final int y, final EntityType<?> entityType) {
        final Entity entity = getRenderEntity(entityType);
        if (entity == null) {
            return;
        }

        entity.setLevel(menu.getPlayer().level());

        final EntityRenderState renderState = extractRenderState(entity);
        if (renderState == null) {
            return;
        }

        final EntityDimensions bounds = entityType.getDimensions();
        final float size = Math.max(bounds.width(), bounds.height());
        final float scale = 11.0f / size;

        final Quaternionf cameraOrientation = new Quaternionf().rotationX(toRadians(PREVIEW_TILT));
        cameraOrientation.mul(new Quaternionf().rotationY(toRadians(PREVIEW_YAW)));

        final Quaternionf rotation = new Quaternionf().rotationZ(toRadians(180));
        rotation.mul(cameraOrientation);

        final Vector3f translation = new Vector3f(0, renderState.boundingBoxHeight / 2.0f, 0);

        final int screenX = leftPos + x;
        final int screenY = topPos + y;

        graphics.submitEntityRenderState(renderState, scale, translation, rotation, cameraOrientation,
            screenX, screenY, screenX + 16, screenY + 16);
    }

    @Nullable
    private static EntityRenderState extractRenderState(final Entity entity) {
        final EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        final EntityRenderer<? super Entity, ?> renderer = dispatcher.getRenderer(entity);
        final EntityRenderState renderState = renderer.createRenderState(entity, 1.0f);
        renderState.lightCoords = 0xf000f0;
        renderState.shadowPieces.clear();
        renderState.outlineColor = EntityRenderState.NO_OUTLINE;

        if (renderState instanceof final LivingEntityRenderState livingRenderState) {
            livingRenderState.bodyRot = 180;
            livingRenderState.yRot = 0;
            livingRenderState.xRot = 0;
        }

        return renderState;
    }

    @Nullable
    private Entity getRenderEntity(final EntityType<?> entityType) {
        return RENDER_ENTITIES.computeIfAbsent(entityType, t -> t.create(menu.getPlayer().level(), EntitySpawnReason.LOAD));
    }
}
