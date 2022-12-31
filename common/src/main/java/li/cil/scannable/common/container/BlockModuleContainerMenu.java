package li.cil.scannable.common.container;

import li.cil.scannable.common.item.ConfigurableBlockScannerModuleItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class BlockModuleContainerMenu extends AbstractModuleContainerMenu {
    public static BlockModuleContainerMenu create(final int id, final Inventory playerInventory, final FriendlyByteBuf data) {
        final InteractionHand hand = data.readEnum(InteractionHand.class);
        return new BlockModuleContainerMenu(id, playerInventory, hand);
    }

    // --------------------------------------------------------------------- //

    public BlockModuleContainerMenu(final int windowId, final Inventory inventory, final InteractionHand hand) {
        super(Containers.BLOCK_MODULE_CONTAINER.get(), windowId, inventory, hand);
    }

    @Override
    public void removeItemAt(final int index) {
        ConfigurableBlockScannerModuleItem.removeBlockAt(getPlayer().getItemInHand(getHand()), index);
    }

    @Override
    public void setItemAt(final int index, final ResourceLocation name) {
        BuiltInRegistries.BLOCK.getOptional(name).ifPresent(block -> {
            final ItemStack stack = getPlayer().getItemInHand(getHand());
            ConfigurableBlockScannerModuleItem.setBlockAt(stack, index, block);
        });
    }
}
