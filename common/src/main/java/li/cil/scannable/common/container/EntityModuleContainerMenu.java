package li.cil.scannable.common.container;

import li.cil.scannable.common.item.ConfigurableEntityScannerModuleItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public final class EntityModuleContainerMenu extends AbstractModuleContainerMenu {
    public static EntityModuleContainerMenu create(final int windowId, final Inventory inventory, final FriendlyByteBuf buffer) {
        final InteractionHand hand = buffer.readEnum(InteractionHand.class);
        return new EntityModuleContainerMenu(windowId, inventory, hand);
    }

    // --------------------------------------------------------------------- //

    public EntityModuleContainerMenu(final int windowId, final Inventory inventory, final InteractionHand hand) {
        super(Containers.ENTITY_MODULE_CONTAINER.get(), windowId, inventory, hand);
    }

    @Override
    public void removeItemAt(final int index) {
        final ItemStack stack = getPlayer().getItemInHand(getHand());
        ConfigurableEntityScannerModuleItem.removeEntityTypeAt(stack, index);
    }

    @Override
    public void setItemAt(final int index, final ResourceLocation name) {
        final ItemStack stack = getPlayer().getItemInHand(getHand());
        BuiltInRegistries.ENTITY_TYPE.getOptional(name).ifPresent(type ->
            ConfigurableEntityScannerModuleItem.setEntityTypeAt(stack, index, type));
    }
}
