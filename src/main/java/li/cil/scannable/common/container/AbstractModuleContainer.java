package li.cil.scannable.common.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public abstract class AbstractModuleContainer extends Container {
    private final PlayerEntity player;
    private final Hand hand;
    private final ItemStack stack;

    // --------------------------------------------------------------------- //

    protected AbstractModuleContainer(final ContainerType<?> type, final int windowId, final PlayerInventory inventory, final Hand hand) {
        super(type, windowId);

        this.player = inventory.player;
        this.hand = hand;
        this.stack = player.getHeldItem(hand);

        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, row * 18 + 51));
            }
        }

        for (int slot = 0; slot < 9; ++slot) {
            addSlot(new Slot(inventory, slot, 8 + slot * 18, 109));
        }
    }

    public PlayerEntity getPlayer() {
        return player;
    }

    public Hand getHand() {
        return hand;
    }

    public abstract void removeItemAt(final int index);

    public abstract void setItemAt(final int index, final String value);

    // --------------------------------------------------------------------- //
    // Container

    @Override
    public boolean canInteractWith(final PlayerEntity player) {
        return player == this.player && ItemStack.areItemStacksEqual(player.getHeldItem(hand), stack);
    }

    @Override
    public ItemStack transferStackInSlot(final PlayerEntity player, final int index) {
        return ItemStack.EMPTY;
    }
}
