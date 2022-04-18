package li.cil.scannable.common.inventory;

import li.cil.scannable.common.item.ScannerItem;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public final class ScannerItemHandler extends SimpleContainer {
    public static final int ACTIVE_MODULE_COUNT = 3;
    public static final int TOTAL_MODULE_COUNT = ACTIVE_MODULE_COUNT;

    private static final String TAG_ITEMS = "items";

    private final ItemStack container;

    public ScannerItemHandler(final ItemStack container) {
        super(TOTAL_MODULE_COUNT);
        this.container = container;
    }

    public static ScannerItemHandler of(final ItemStack container) {
        if(!(container.getItem() instanceof ScannerItem))
            return null;
        ScannerItemHandler handler = new ScannerItemHandler(container);
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

    private NonNullList<ItemStack> getItemsInRange(int start, int end) {
        NonNullList<ItemStack> list = NonNullList.withSize(end-start, ItemStack.EMPTY);
        for(int i = start; i < end; i++) {
            list.set(i-start, this.getItem(i));
        }
        return list;
    }

    public NonNullList<ItemStack> getActiveModules() {
        return getItemsInRange(0, ACTIVE_MODULE_COUNT);
    }
}
