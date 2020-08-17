package li.cil.scannable.common.container;

import li.cil.scannable.common.inventory.ItemHandlerScanner;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.Hand;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public final class ScannerContainerProvider extends AbstractHeldItemStackContainerProvider {
    public ScannerContainerProvider(final PlayerEntity player, final Hand hand) {
        super(player, hand);
    }

    @Override
    protected Container createContainer(final int windowId, final PlayerInventory inventory, final Hand hand, @Nullable final IItemHandler itemHandler) {
        if (itemHandler instanceof ItemHandlerScanner) {
            return ContainerScanner.createForServer(windowId, inventory, hand, (ItemHandlerScanner) itemHandler);
        } else {
            return null;
        }
    }
}
