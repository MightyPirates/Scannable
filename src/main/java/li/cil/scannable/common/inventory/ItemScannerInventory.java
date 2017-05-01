package li.cil.scannable.common.inventory;

import li.cil.scannable.common.capabilities.CapabilityScanResultProvider;
import li.cil.scannable.common.item.AbstractItemScannerModule;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ItemScannerInventory extends ItemStackHandler implements ICapabilityProvider {
    private static final String TAG_ITEMS = "items";

    private final ItemStack container;

    // --------------------------------------------------------------------- //

    public ItemScannerInventory(final ItemStack container) {
        super(3);
        this.container = container;
    }

    // --------------------------------------------------------------------- //
    // ICapabilityProvider

    @Override
    public boolean hasCapability(@Nonnull final Capability<?> capability, @Nullable final EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> T getCapability(@Nonnull final Capability<T> capability, @Nullable final EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            super.deserializeNBT(container.getOrCreateSubCompound(TAG_ITEMS));
            return (T) this;
        }
        return null;
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
        container.setTagInfo(TAG_ITEMS, super.serializeNBT());
    }

    // --------------------------------------------------------------------- //
    // INBTSerializable

    // HACKS! Don't serialize this into the common capability NBT part of the
    // item stack but directly into the item stack's general compound tag.
    // Why? Because https://github.com/MinecraftForge/MinecraftForge/issues/3751
    // We need the inventory info on the client so that we can figure out what
    // providers to use and what module effects to apply. We could still also
    // write to the capability NBT, but data duplication is meh, so we don't.

    @Override
    public NBTTagCompound serializeNBT() {
        return new NBTTagCompound();
    }

    @Override
    public void deserializeNBT(final NBTTagCompound nbt) {
    }
}
