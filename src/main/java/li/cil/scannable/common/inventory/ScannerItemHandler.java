package li.cil.scannable.common.inventory;

import li.cil.scannable.common.item.ScannerItem;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public final class ScannerItemHandler extends SimpleContainer {
    public static final int ACTIVE_MODULE_COUNT = 3;
    public static final int INACTIVE_MODULE_COUNT = 6;
    public static final int TOTAL_MODULE_COUNT = ACTIVE_MODULE_COUNT + INACTIVE_MODULE_COUNT;

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

    public void updateFromNBT() {
        final CompoundTag tag = container.getTag();
        if (tag != null && tag.contains(TAG_ITEMS, Tag.TAG_LIST)) {
            this.fromTag(tag.getList(TAG_ITEMS, Tag.TAG_COMPOUND));
            /*
            if (stacks.size() != TOTAL_MODULE_COUNT) {
                final List<ItemStack> oldStacks = new ArrayList<>(stacks);
                setSize(TOTAL_MODULE_COUNT);
                final int count = Math.min(TOTAL_MODULE_COUNT, oldStacks.size());
                for (int slot = 0; slot < count; slot++) {
                    stacks.set(slot, oldStacks.get(slot));
                }
            }
             */
        }
    }

    public void saveToNBT() {
        CompoundTag tag = new CompoundTag();
        tag.put(TAG_ITEMS, this.createTag());
        this.container.setTag(tag);
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

    public NonNullList<ItemStack> getInactiveModules() {
        return getItemsInRange(ACTIVE_MODULE_COUNT, TOTAL_MODULE_COUNT);
    }

    // --------------------------------------------------------------------- //
    // IItemHandler

    /*
    @Override
    protected int getStackLimit(final int slot, @Nonnull final ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }

        // All built-in modules, including those without capability such as the range module.
        if (stack.getItem() instanceof ScannerModuleItem) {
            return 64;
        }

        // External modules declared via capability.
        final LazyOptional<ScannerModule> module = stack.getCapability(Capabilities.SCANNER_MODULE_CAPABILITY);
        if (module.isPresent()) {
            return 64;
        }

        return 0;
    }

     */
}
