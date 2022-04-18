package li.cil.scannable.common.inventory;

import li.cil.scannable.common.item.ScannerItem;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public final class ScannerItemHandler extends SimpleContainer {
    public static final int ACTIVE_MODULE_COUNT = 3;
    public static final int TOTAL_MODULE_COUNT = ACTIVE_MODULE_COUNT;

    private static final String TAG_ITEMS = "items";
    private static final String TAG_SLOT = "slot";

    private final ItemStack container;

    public ScannerItemHandler(final ItemStack container) {
        super(TOTAL_MODULE_COUNT);
        this.container = container;
    }

    @Nullable
    public static ScannerItemHandler of(final ItemStack container) {
        if (!(container.getItem() instanceof ScannerItem))
            return null;
        final ScannerItemHandler handler = new ScannerItemHandler(container);
        handler.updateFromNBT();
        return handler;
    }

    @Override
    public void setChanged() {
        this.saveToNBT();
    }

    public void updateFromNBT() {
        final CompoundTag tag = container.getTag();
        if (tag != null && tag.contains(TAG_ITEMS, Tag.TAG_LIST)) {
            this.fromTag(tag.getList(TAG_ITEMS, Tag.TAG_COMPOUND));
        }
    }

    public void saveToNBT() {
        this.container.getOrCreateTag().put(TAG_ITEMS, this.createTag());
    }

    private NonNullList<ItemStack> getItemsInRange(final int start, final int end) {
        final NonNullList<ItemStack> list = NonNullList.withSize(end - start, ItemStack.EMPTY);
        for (int i = start; i < end; i++) {
            list.set(i - start, this.getItem(i));
        }
        return list;
    }

    public NonNullList<ItemStack> getActiveModules() {
        return getItemsInRange(0, ACTIVE_MODULE_COUNT);
    }

    // --------------------------------------------------------------------- //
    // SimpleContainer

    public void fromTag(final ListTag tag) {
        for (int i = 0; i < this.getContainerSize(); ++i) {
            this.setItem(i, ItemStack.EMPTY);
        }

        for (int i = 0; i < tag.size(); ++i) {
            final CompoundTag slotTag = tag.getCompound(i);
            final int slot = slotTag.getByte(TAG_SLOT);
            if (slot >= 0 && slot < this.getContainerSize()) {
                slotTag.remove(TAG_SLOT);
                this.setItem(slot, ItemStack.of(slotTag));
            }
        }
    }

    public ListTag createTag() {
        final ListTag tag = new ListTag();

        for (int i = 0; i < this.getContainerSize(); ++i) {
            final ItemStack stack = this.getItem(i);
            if (!stack.isEmpty()) {
                final CompoundTag slotTag = new CompoundTag();
                stack.save(slotTag);
                slotTag.putByte(TAG_SLOT, (byte) i);
                tag.add(slotTag);
            }
        }

        return tag;
    }
}
