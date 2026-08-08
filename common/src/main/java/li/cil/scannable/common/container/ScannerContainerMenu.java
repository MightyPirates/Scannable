package li.cil.scannable.common.container;

import li.cil.scannable.common.inventory.ScannerContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class ScannerContainerMenu extends AbstractContainerMenu {
    public static ScannerContainerMenu create(final int windowId, final Inventory inventory, final FriendlyByteBuf buffer) {
        final InteractionHand hand = buffer.readEnum(InteractionHand.class);
        return new ScannerContainerMenu(windowId, inventory, hand, new ScannerContainer(inventory.player.getItemInHand(hand)));
    }

    // --------------------------------------------------------------------- //

    private final Player player;
    private final InteractionHand hand;
    private final ItemStack stack;

    // --------------------------------------------------------------------- //

    public ScannerContainerMenu(final int windowId, final Inventory inventory, final InteractionHand hand, final ScannerContainer itemHandler) {
        super(Containers.SCANNER_CONTAINER.get(), windowId);

        this.player = inventory.player;
        this.hand = hand;
        this.stack = player.getItemInHand(hand);

        final Container activeModules = itemHandler.getActiveModules();
        for (int slot = 0; slot < activeModules.getContainerSize(); ++slot) {
            addSlot(new ScannerSlot(activeModules, slot, 62 + slot * 18, 20));
        }

        final Container storedModules = itemHandler.getInactiveModules();
        for (int slot = 0; slot < storedModules.getContainerSize(); ++slot) {
            addSlot(new ScannerSlot(storedModules, slot, 62 + slot * 18, 46));
        }

        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                addSlot(new ScannerSlot(inventory, col + row * 9 + 9, 8 + col * 18, row * 18 + 77));
            }
        }

        for (int slot = 0; slot < 9; ++slot) {
            addSlot(new ScannerSlot(inventory, slot, 8 + slot * 18, 135));
        }
    }

    public Player getPlayer() {
        return player;
    }

    public InteractionHand getHand() {
        return hand;
    }

    // --------------------------------------------------------------------- //
    // Container

    @Override
    public boolean stillValid(final Player player) {
        return player == this.player && ItemStack.matches(player.getItemInHand(hand), stack);
    }

    @Override
    public ItemStack quickMoveStack(final Player player, final int index) {
        final Slot from = slots.get(index);
        final ItemStack stack = from.getItem().copy();
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        final boolean intoPlayerInventory = from.container != player.getInventory();
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

                final boolean itemsAreEqual = ItemStack.isSameItemSameComponents(fromStack, intoStack);
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
