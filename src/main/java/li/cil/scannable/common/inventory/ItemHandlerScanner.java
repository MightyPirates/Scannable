package li.cil.scannable.common.inventory;

import li.cil.scannable.common.capabilities.CapabilityScanResultProvider;
import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.item.AbstractItemScannerModule;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public final class ItemHandlerScanner extends ItemStackHandler {
    private static final String TAG_ITEMS = "items";

    private final ItemStack container;

    public ItemHandlerScanner(final ItemStack container) {
        super(Constants.SCANNER_TOTAL_MODULE_COUNT);
        this.container = container;
    }

    public void updateFromNBT() {
        final NBTTagCompound nbt = container.getTagCompound();
        if (nbt != null && nbt.hasKey(TAG_ITEMS, NBT.TAG_COMPOUND)) {
            deserializeNBT((NBTTagCompound) nbt.getTag(TAG_ITEMS));
            if (stacks.size() != Constants.SCANNER_TOTAL_MODULE_COUNT) {
                final List<ItemStack> oldStacks = new ArrayList<>(stacks);
                setSize(Constants.SCANNER_TOTAL_MODULE_COUNT);
                final int count = Math.min(Constants.SCANNER_TOTAL_MODULE_COUNT, oldStacks.size());
                for (int slot = 0; slot < count; slot++) {
                    stacks.set(slot, oldStacks.get(slot));
                }
            }
        }
    }

    public IItemHandler getActiveModules() {
        return new RangedWrapper(this, 0, Constants.SCANNER_ACTIVE_MODULE_COUNT);
    }

    public IItemHandler getInactiveModules() {
        return new RangedWrapper(this, Constants.SCANNER_ACTIVE_MODULE_COUNT, Constants.SCANNER_TOTAL_MODULE_COUNT);
    }

    // --------------------------------------------------------------------- //
    // IItemHandler

    @Override
    protected int getStackLimit(final int slot, @Nonnull final ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        if (stack.getItem() instanceof AbstractItemScannerModule) {
            return 64;
        }
        if (stack.hasCapability(CapabilityScanResultProvider.SCAN_RESULT_PROVIDER_CAPABILITY, null)) {
            return 64;
        }
        return 0;
    }

    // --------------------------------------------------------------------- //
    // ItemStackHandler

    @Override
    protected void onContentsChanged(final int slot) {
        super.onContentsChanged(slot);
        container.setTagInfo(TAG_ITEMS, serializeNBT());
    }
}
