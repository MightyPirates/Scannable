package li.cil.scannable.common.container;

import li.cil.scannable.util.LazyOptionalUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public abstract class AbstractHeldItemStackContainerProvider implements INamedContainerProvider {
    protected final PlayerEntity player;
    protected final Hand hand;

    protected AbstractHeldItemStackContainerProvider(final PlayerEntity player, final Hand hand) {
        this.player = player;
        this.hand = hand;
    }

    @Override
    public ITextComponent getDisplayName() {
        return player.getHeldItem(hand).getDisplayName();
    }

    @Nullable
    @Override
    public Container createMenu(final int windowId, final PlayerInventory inventory, final PlayerEntity player) {
        final LazyOptional<IItemHandler> capability = player.getHeldItem(hand).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
        final IItemHandler itemHandler = LazyOptionalUtil.orNull(capability);
        return createContainer(windowId, inventory, hand, itemHandler);
    }

    @Nullable
    protected abstract Container createContainer(final int windowId, final PlayerInventory inventory, final Hand hand, @Nullable final IItemHandler itemHandler);
}
