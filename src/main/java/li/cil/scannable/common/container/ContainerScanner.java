package li.cil.scannable.common.container;

import li.cil.scannable.common.Scannable;
import li.cil.scannable.common.inventory.ItemHandlerScanner;
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
    private final ItemStack stack;

    public static ContainerScanner createForServer(final int windowId, final PlayerInventory inventory, final Hand hand, final ItemHandlerScanner itemHandler) {
        return new ContainerScanner(windowId, inventory, hand, itemHandler);
    }

    public static ContainerScanner createForClient(final int windowId, final PlayerInventory inventory, final PacketBuffer buffer) {
        final Hand hand = buffer.readEnum(Hand.class);
        return new ContainerScanner(windowId, inventory, hand, new ItemHandlerScanner(inventory.player.getItemInHand(hand)));
    }

    // --------------------------------------------------------------------- //

    public ContainerScanner(final int windowId, final PlayerInventory inventory, final Hand hand, final ItemHandlerScanner itemHandler) {
        super(Scannable.SCANNER_CONTAINER.get(), windowId);

        this.player = inventory.player;
        this.hand = hand;
        this.stack = player.getItemInHand(hand);

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
    public boolean stillValid(final PlayerEntity player) {
        return player == this.player && ItemStack.matches(player.getItemInHand(hand), stack);
    }

    @Override
    public ItemStack quickMoveStack(final PlayerEntity player, final int index) {
        final Slot from = slots.get(index);
        if (from == null) {
            return ItemStack.EMPTY;
        }
        final ItemStack stack = from.getItem().copy();
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        final boolean intoPlayerInventory = from.container != player.inventory;
        final ItemStack fromStack = from.getItem();

        final int step, begin;
        if (intoPlayerInventory) {
            step = -1;
            begin = slots.size() - 1;
        } else {
            step = 1;
            begin = 0;
        }

        if (fromStack.getMaxStackSize() > 1) {
            for (int i = begin; i >= 0 && i < slots.size(); i += step) {
                final Slot into = slots.get(i);
                if (into.container == from.container) {
                    continue;
                }

                final ItemStack intoStack = into.getItem();
                if (intoStack.isEmpty()) {
                    continue;
                }

                final boolean itemsAreEqual = fromStack.sameItem(intoStack) && ItemStack.tagMatches(fromStack, intoStack);
                if (!itemsAreEqual) {
                    continue;
                }

                final int maxSizeInSlot = Math.min(fromStack.getMaxStackSize(), into.getMaxStackSize(stack));
                final int spaceInSlot = maxSizeInSlot - intoStack.getCount();
                if (spaceInSlot <= 0) {
                    continue;
                }

                final int itemsMoved = Math.min(spaceInSlot, fromStack.getCount());
                if (itemsMoved <= 0) {
                    continue;
                }

                intoStack.grow(from.remove(itemsMoved).getCount());
                into.setChanged();

                if (from.getItem().isEmpty()) {
                    break;
                }
            }
        }

        for (int i = begin; i >= 0 && i < slots.size(); i += step) {
            if (from.getItem().isEmpty()) {
                break;
            }

            final Slot into = slots.get(i);
            if (into.container == from.container) {
                continue;
            }

            if (into.hasItem()) {
                continue;
            }

            if (!into.mayPlace(fromStack)) {
                continue;
            }

            final int maxSizeInSlot = Math.min(fromStack.getMaxStackSize(), into.getMaxStackSize(fromStack));
            final int itemsMoved = Math.min(maxSizeInSlot, fromStack.getCount());
            into.set(from.remove(itemsMoved));
        }

        return from.getItem().getCount() < stack.getCount() ? from.getItem() : ItemStack.EMPTY;
    }
}
