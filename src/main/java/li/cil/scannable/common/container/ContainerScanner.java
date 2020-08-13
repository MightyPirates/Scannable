package li.cil.scannable.common.container;

import li.cil.scannable.common.Scannable;
import li.cil.scannable.common.inventory.ItemHandlerScanner;
import li.cil.scannable.common.item.ItemScanner;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public final class ContainerScanner extends Container {
    private final PlayerEntity player;
    private final Hand hand;

    public static ContainerScanner createForServer(final int windowId, final PlayerInventory inventory, final Hand hand, final ItemHandlerScanner itemHandler) {
        return new ContainerScanner(windowId, inventory, hand, itemHandler);
    }

    public static ContainerScanner createForClient(final int windowId, final PlayerInventory inventory, final PacketBuffer buffer) {
        final Hand hand = buffer.readEnumValue(Hand.class);
        return new ContainerScanner(windowId, inventory, hand, new ItemHandlerScanner(inventory.player.getHeldItem(hand)));
    }

    // --------------------------------------------------------------------- //

    public ContainerScanner(final int windowId, final PlayerInventory inventory, final Hand hand, final ItemHandlerScanner itemHandler) {
        super(Scannable.SCANNER_CONTAINER.get(), windowId);

        this.player = inventory.player;
        this.hand = hand;

        final IItemHandler activeModules = itemHandler.getActiveModules();
        for (int slot = 0; slot < activeModules.getSlots(); ++slot) {
            addSlot(new SlotItemHandler(activeModules, slot, 62 + slot * 18, 20));
        }

        final IItemHandler storedModules = itemHandler.getInactiveModules();
        for (int slot = 0; slot < storedModules.getSlots(); ++slot) {
            addSlot(new SlotItemHandler(storedModules, slot, 62 + slot * 18, 46));
        }

        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, row * 18 + 77));
            }
        }

        for (int slot = 0; slot < 9; ++slot) {
            addSlot(new Slot(inventory, slot, 8 + slot * 18, 135));
        }
    }

    public Hand getHand() {
        return hand;
    }

    // --------------------------------------------------------------------- //
    // Container

    @Override
    public boolean canInteractWith(final PlayerEntity player) {
        return player == this.player && ItemScanner.isScanner(player.getHeldItem(hand));
    }

    @Override
    public ItemStack transferStackInSlot(final PlayerEntity player, final int index) {
        final Slot from = inventorySlots.get(index);
        if (from == null) {
            return ItemStack.EMPTY;
        }
        final ItemStack stack = from.getStack().copy();
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        final boolean intoPlayerInventory = from.inventory != player.inventory;
        final ItemStack fromStack = from.getStack();

        final int step, begin;
        if (intoPlayerInventory) {
            step = -1;
            begin = inventorySlots.size() - 1;
        } else {
            step = 1;
            begin = 0;
        }

        if (fromStack.getMaxStackSize() > 1) {
            for (int i = begin; i >= 0 && i < inventorySlots.size(); i += step) {
                final Slot into = inventorySlots.get(i);
                if (into.inventory == from.inventory) {
                    continue;
                }

                final ItemStack intoStack = into.getStack();
                if (intoStack.isEmpty()) {
                    continue;
                }

                final boolean itemsAreEqual = fromStack.isItemEqual(intoStack) && ItemStack.areItemStackTagsEqual(fromStack, intoStack);
                if (!itemsAreEqual) {
                    continue;
                }

                final int maxSizeInSlot = Math.min(fromStack.getMaxStackSize(), into.getItemStackLimit(stack));
                final int spaceInSlot = maxSizeInSlot - intoStack.getCount();
                if (spaceInSlot <= 0) {
                    continue;
                }

                final int itemsMoved = Math.min(spaceInSlot, fromStack.getCount());
                if (itemsMoved <= 0) {
                    continue;
                }

                intoStack.grow(from.decrStackSize(itemsMoved).getCount());
                into.onSlotChanged();

                if (from.getStack().isEmpty()) {
                    break;
                }
            }
        }

        for (int i = begin; i >= 0 && i < inventorySlots.size(); i += step) {
            if (from.getStack().isEmpty()) {
                break;
            }

            final Slot into = inventorySlots.get(i);
            if (into.inventory == from.inventory) {
                continue;
            }

            if (into.getHasStack()) {
                continue;
            }

            if (!into.isItemValid(fromStack)) {
                continue;
            }

            final int maxSizeInSlot = Math.min(fromStack.getMaxStackSize(), into.getItemStackLimit(fromStack));
            final int itemsMoved = Math.min(maxSizeInSlot, fromStack.getCount());
            into.putStack(from.decrStackSize(itemsMoved));
        }

        return from.getStack().getCount() < stack.getCount() ? from.getStack() : ItemStack.EMPTY;
    }
}
