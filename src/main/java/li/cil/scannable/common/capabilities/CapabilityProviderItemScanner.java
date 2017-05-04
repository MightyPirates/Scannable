package li.cil.scannable.common.capabilities;

import li.cil.scannable.common.config.Settings;
import li.cil.scannable.common.energy.EnergyStorageScanner;
import li.cil.scannable.common.inventory.ItemHandlerScanner;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class CapabilityProviderItemScanner implements ICapabilityProvider {
    private final ItemHandlerScanner itemHandler;
    private final EnergyStorageScanner energyStorage;

    // --------------------------------------------------------------------- //

    public CapabilityProviderItemScanner(final ItemStack container) {
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
            itemHandler.updateFromNBT();
            return (T) itemHandler;
        }
        if (Settings.useEnergy && capability == CapabilityEnergy.ENERGY) {
            energyStorage.updateFromNBT();
            return (T) energyStorage;
        }

        return null;
    }
}
