package li.cil.scannable.common.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.Hand;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public final class BlockModuleContainerProvider extends AbstractHeldItemStackContainerProvider {
    public BlockModuleContainerProvider(final PlayerEntity player, final Hand hand) {
        super(player, hand);
    }

    @Override
    protected Container createContainer(final int windowId, final PlayerInventory inventory, final Hand hand, @Nullable final IItemHandler itemHandler) {
        return BlockModuleContainer.createForServer(windowId, inventory, hand);
    }
}
