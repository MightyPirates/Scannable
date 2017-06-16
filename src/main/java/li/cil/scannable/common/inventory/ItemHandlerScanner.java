package li.cil.scannable.common.inventory;

import li.cil.scannable.common.capabilities.CapabilityScanResultProvider;
import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.item.AbstractItemScannerModule;
import li.cil.scannable.util.ItemStackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;

import javax.annotation.Nonnull;
import java.util.Arrays;

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
            if (stacks.length != Constants.SCANNER_TOTAL_MODULE_COUNT) {
                final ItemStack[] oldStacks = Arrays.copyOf(stacks, stacks.length);
                setSize(Constants.SCANNER_TOTAL_MODULE_COUNT);
                final int count = Math.min(Constants.SCANNER_TOTAL_MODULE_COUNT, oldStacks.length);
                System.arraycopy(oldStacks, 0, stacks, 0, count);
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
        if (ItemStackUtils.isEmpty(stack)) {
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
