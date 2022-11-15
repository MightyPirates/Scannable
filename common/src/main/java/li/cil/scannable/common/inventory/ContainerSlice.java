package li.cil.scannable.common.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public final class ContainerSlice implements Container, Iterable<ItemStack> {
    private final Container container;
    private final int offset;
    private final int length;

    // --------------------------------------------------------------------- //

    public ContainerSlice(final Container container, final int offset, final int length) {
        this.container = container;
        this.offset = offset;
        this.length = length;
    }

    // --------------------------------------------------------------------- //
    // Container

    @Override
    public int getContainerSize() {
        return length;
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < length; i++) {
            final ItemStack stack = container.getItem(offset + i);
            if (!stack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack getItem(final int index) {
        return isIndexInBounds(index) ? container.getItem(offset + index) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(final int index, final int count) {
        return isIndexInBounds(index) ? container.removeItem(offset + index, count) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(final int index) {
        return isIndexInBounds(index) ? container.removeItemNoUpdate(offset + index) : ItemStack.EMPTY;
    }

    @Override
    public void setItem(final int index, final ItemStack stack) {
        if (isIndexInBounds(index)) {
            container.setItem(offset + index, stack);
        }
    }

    @Override
    public void setChanged() {
        container.setChanged();
    }

    @Override
    public boolean stillValid(final Player player) {
        return container.stillValid(player);
    }

    @Override
    public void startOpen(final Player player) {
        container.startOpen(player);
    }

    @Override
    public void stopOpen(final Player player) {
        container.stopOpen(player);
    }

    @Override
    public boolean canPlaceItem(final int i, final ItemStack itemStack) {
        return container.canPlaceItem(i, itemStack);
    }

    // --------------------------------------------------------------------- //
    // Clearable

    @Override
    public void clearContent() {
        for (int i = 0; i < length; i++) {
            container.removeItemNoUpdate(offset + i);
        }

        container.setChanged();
    }

    // --------------------------------------------------------------------- //
    // Iterable<ItemStack>

    @NotNull
    @Override
    public Iterator<ItemStack> iterator() {
        return new Iterator<>() {
            private int index;

            @Override
            public boolean hasNext() {
                return index < getContainerSize();
            }

            @Override
            public ItemStack next() {
                final ItemStack stack = getItem(index);
                index++;
                return stack;
            }
        };
    }

    // --------------------------------------------------------------------- //

    private boolean isIndexInBounds(final int index) {
        return index >= 0 && index < length;
    }
}
