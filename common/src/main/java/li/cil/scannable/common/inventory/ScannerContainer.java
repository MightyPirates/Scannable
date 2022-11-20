package li.cil.scannable.common.inventory;

import li.cil.scannable.common.item.Items;
import li.cil.scannable.common.item.ScannerItem;
import li.cil.scannable.common.item.ScannerModuleItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public final class ScannerContainer extends SimpleContainer {
    private static final int ACTIVE_MODULE_COUNT = 3;
    private static final int INACTIVE_MODULE_COUNT = 6;
    private static final int TOTAL_MODULE_COUNT = ACTIVE_MODULE_COUNT + INACTIVE_MODULE_COUNT;

    private static final String TAG_ITEMS = "items";
    private static final String TAG_SLOT = "slot";
    private static final String TAG_ITEM = "item";

    private final ItemStack container;

    public ScannerContainer(final ItemStack container) {
        super(TOTAL_MODULE_COUNT);
        this.container = container;

        final CompoundTag tag = container.getTag();
        if (tag != null && tag.contains(TAG_ITEMS, Tag.TAG_LIST)) {
            fromTag(tag.getList(TAG_ITEMS, Tag.TAG_COMPOUND));
        }
    }

    public static ScannerContainer of(final ItemStack container) {
        if (container.getItem() instanceof ScannerItem) {
            return new ScannerContainer(container);
        } else {
            return new ScannerContainer(new ItemStack(Items.SCANNER.get()));
        }
    }

    public ContainerSlice getActiveModules() {
        return new ContainerSlice(this, 0, ACTIVE_MODULE_COUNT);
    }

    public ContainerSlice getInactiveModules() {
        return new ContainerSlice(this, ACTIVE_MODULE_COUNT, TOTAL_MODULE_COUNT);
    }

    // --------------------------------------------------------------------- //
    // Container

    @Override
    public void setItem(final int i, final ItemStack itemStack) {
        if (canPlaceItem(i, itemStack)) {
            super.setItem(i, itemStack);
        }
    }

    @Override
    public boolean canPlaceItem(final int i, final ItemStack stack) {
        return isModule(stack) && super.canPlaceItem(i, stack);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        this.container.addTagElement(TAG_ITEMS, this.createTag());
    }

    // --------------------------------------------------------------------- //
    // SimpleContainer

    @Override
    public ItemStack addItem(final ItemStack stack) {
        if (canAddItem(stack)) {
            return super.addItem(stack);
        } else {
            return stack;
        }
    }

    @Override
    public boolean canAddItem(final ItemStack stack) {
        return isModule(stack) && super.canAddItem(stack);
    }

    @Override
    public void fromTag(final ListTag tag) {
        for (int i = 0; i < this.getContainerSize(); ++i) {
            this.setItem(i, ItemStack.EMPTY);
        }

        for (int i = 0; i < tag.size(); ++i) {
            final CompoundTag slotTag = tag.getCompound(i);
            final int slot = slotTag.getByte(TAG_SLOT) & 0xFF;
            if (slot < this.getContainerSize()) {
                this.setItem(slot, ItemStack.of(slotTag.getCompound(TAG_ITEM)));
            }
        }
    }

    @Override
    public ListTag createTag() {
        final ListTag tag = new ListTag();

        for (int i = 0; i < this.getContainerSize(); ++i) {
            final ItemStack stack = this.getItem(i);
            if (!stack.isEmpty()) {
                final CompoundTag slotTag = new CompoundTag();
                slotTag.putByte(TAG_SLOT, (byte) i);

                final CompoundTag itemTag = new CompoundTag();
                stack.save(itemTag);
                slotTag.put(TAG_ITEM, itemTag);

                tag.add(slotTag);
            }
        }

        return tag;
    }

    // --------------------------------------------------------------------- //

    private boolean isModule(final ItemStack stack) {
        // All built-in modules, including those without capability such as the range module.
        if (stack.getItem() instanceof ScannerModuleItem) {
            return true;
        }

        // External modules declared via capability/interface.
        if (ScannerModuleItem.getModule(stack).isPresent()) {
            return true;
        }

        return false;
    }
}
