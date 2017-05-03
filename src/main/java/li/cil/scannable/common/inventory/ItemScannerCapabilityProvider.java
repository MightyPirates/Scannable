package li.cil.scannable.common.inventory;

import li.cil.scannable.common.capabilities.CapabilityScanResultProvider;
import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.config.Settings;
import li.cil.scannable.common.item.AbstractItemScannerModule;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ItemScannerCapabilityProvider implements ICapabilityProvider {
    private static final String TAG_ITEMS = "items";
    private static final String TAG_ENERGY = "energy";

    private final ItemHandlerScanner itemHandler;
    private final EnergyStorageScanner energyStorage;

    // --------------------------------------------------------------------- //

    public ItemScannerCapabilityProvider(final ItemStack container) {
        itemHandler = new ItemHandlerScanner(container);
        energyStorage = new EnergyStorageScanner(container);
    }

    // --------------------------------------------------------------------- //
    // ICapabilityProvider

    @Override
    public boolean hasCapability(@Nonnull final Capability<?> capability, @Nullable final EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return true;
        }
        if (Settings.useEnergy && capability == CapabilityEnergy.ENERGY) {
            return true;
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> T getCapability(@Nonnull final Capability<T> capability, @Nullable final EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            final NBTTagCompound nbt = itemHandler.container.getTagCompound();
            if (nbt != null && nbt.hasKey(TAG_ITEMS, NBT.TAG_COMPOUND)) {
                itemHandler.deserializeNBT((NBTTagCompound) nbt.getTag(TAG_ITEMS));
            }
            return (T) itemHandler;
        }
        if (Settings.useEnergy && capability == CapabilityEnergy.ENERGY) {
            final NBTTagCompound nbt = itemHandler.container.getTagCompound();
            if (nbt != null && nbt.hasKey(TAG_ENERGY, NBT.TAG_INT)) {
                energyStorage.deserializeNBT((NBTTagInt) nbt.getTag(TAG_ENERGY));
            }
            return (T) energyStorage;
        }

        return null;
    }

    private static final class ItemHandlerScanner extends ItemStackHandler {
        private final ItemStack container;

        ItemHandlerScanner(final ItemStack container) {
            super(Constants.SCANNER_MAX_MODULE_COUNT);
            this.container = container;
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

    private static final class EnergyStorageScanner extends EnergyStorage implements INBTSerializable<NBTTagInt> {
        private final ItemStack container;

        EnergyStorageScanner(final ItemStack container) {
            super(Constants.SCANNER_ENERGY_CAPACITY);
            this.container = container;
        }

        // --------------------------------------------------------------------- //
        // IEnergyStorage

        @Override
        public int receiveEnergy(final int maxReceive, final boolean simulate) {
            final int result = super.receiveEnergy(maxReceive, simulate);
            if (!simulate && result != 0) {
                container.setTagInfo(TAG_ENERGY, serializeNBT());
            }
            return result;
        }

        @Override
        public int extractEnergy(final int maxExtract, final boolean simulate) {
            final int result = super.extractEnergy(maxExtract, simulate);
            if (!simulate && result != 0) {
                container.setTagInfo(TAG_ENERGY, serializeNBT());
            }
            return result;
        }

        // --------------------------------------------------------------------- //
        // INBTSerializable

        @Override
        public NBTTagInt serializeNBT() {
            return new NBTTagInt(energy);
        }

        @Override
        public void deserializeNBT(final NBTTagInt nbt) {
            energy = nbt.getInt();
        }
    }
}
