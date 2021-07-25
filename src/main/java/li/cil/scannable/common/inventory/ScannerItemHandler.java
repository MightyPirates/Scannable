package li.cil.scannable.common.inventory;

import li.cil.scannable.api.scanning.ScannerModule;
import li.cil.scannable.common.capabilities.Capabilities;
import li.cil.scannable.common.item.ScannerModuleItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public final class ScannerItemHandler extends ItemStackHandler {
    private static final int ACTIVE_MODULE_COUNT = 3;
    private static final int INACTIVE_MODULE_COUNT = 6;
    private static final int TOTAL_MODULE_COUNT = ACTIVE_MODULE_COUNT + INACTIVE_MODULE_COUNT;

    private static final String TAG_ITEMS = "items";

    private final ItemStack container;

    public ScannerItemHandler(final ItemStack container) {
        super(TOTAL_MODULE_COUNT);
        this.container = container;
    }

    public void updateFromNBT() {
        final CompoundTag tag = container.getTag();
        if (tag != null && tag.contains(TAG_ITEMS, NBT.TAG_COMPOUND)) {
            deserializeNBT(tag.getCompound(TAG_ITEMS));
            if (stacks.size() != TOTAL_MODULE_COUNT) {
                final List<ItemStack> oldStacks = new ArrayList<>(stacks);
                setSize(TOTAL_MODULE_COUNT);
                final int count = Math.min(TOTAL_MODULE_COUNT, oldStacks.size());
                for (int slot = 0; slot < count; slot++) {
                    stacks.set(slot, oldStacks.get(slot));
                }
            }
        }
    }

    public IItemHandler getActiveModules() {
        return new RangedWrapper(this, 0, ACTIVE_MODULE_COUNT);
    }

    public IItemHandler getInactiveModules() {
        return new RangedWrapper(this, ACTIVE_MODULE_COUNT, TOTAL_MODULE_COUNT);
    }

    // --------------------------------------------------------------------- //
    // IItemHandler

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

    // --------------------------------------------------------------------- //
    // ItemStackHandler

    @Override
    protected void onContentsChanged(final int slot) {
        super.onContentsChanged(slot);
        container.addTagElement(TAG_ITEMS, serializeNBT());
    }
}
