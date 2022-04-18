package li.cil.scannable.common.inventory;

import li.cil.scannable.common.item.Items;
import li.cil.scannable.common.item.ScannerItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public final class ScannerItemHandler extends SimpleContainer {
    public static final int ACTIVE_MODULE_COUNT = 3;
    private static final int INACTIVE_MODULE_COUNT = 6;
    private static final int TOTAL_MODULE_COUNT = ACTIVE_MODULE_COUNT + INACTIVE_MODULE_COUNT;

    private static final String TAG_ITEMS = "items";
    private static final String TAG_SLOT = "slot";

    private final ItemStack container;

    public ScannerItemHandler(final ItemStack container) {
        super(TOTAL_MODULE_COUNT);
        this.container = container;
    }

    public static ScannerItemHandler of(final ItemStack container) {
        if (container.getItem() instanceof ScannerItem) {
            final ScannerItemHandler handler = new ScannerItemHandler(container);
            handler.updateFromNBT();
            return handler;
        } else {
            return new ScannerItemHandler(new ItemStack(Items.SCANNER.get()));
        }
    }

    public void updateFromNBT() {
        final CompoundTag tag = container.getTag();
        if (tag != null && tag.contains(TAG_ITEMS, Tag.TAG_LIST)) {
            this.fromTag(tag.getList(TAG_ITEMS, Tag.TAG_COMPOUND));
        }
    }

    public ContainerSlice getActiveModules() {
        return new ContainerSlice(this, 0, ACTIVE_MODULE_COUNT);
    }

    public ContainerSlice getInactiveModules() {
        return new ContainerSlice(this, ACTIVE_MODULE_COUNT, INACTIVE_MODULE_COUNT);
    }

    // --------------------------------------------------------------------- //
    // Container

    @Override
    public void setChanged() {
        super.setChanged();
        this.container.getOrCreateTag().put(TAG_ITEMS, this.createTag());
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
