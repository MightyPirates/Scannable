package li.cil.scannable.common.capabilities;

import li.cil.scannable.common.config.Settings;
import li.cil.scannable.common.energy.EnergyStorageScanner;
import li.cil.scannable.common.inventory.ItemHandlerScanner;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class CapabilityProviderScanner implements ICapabilityProvider {
    private final ItemHandlerScanner itemHandler;
    private final EnergyStorageScanner energyStorage;

    // --------------------------------------------------------------------- //

    public CapabilityProviderScanner(final ItemStack container) {
        itemHandler = new ItemHandlerScanner(container);
        energyStorage = new EnergyStorageScanner(container);
    }

    // --------------------------------------------------------------------- //
    // ICapabilityProvider

    @SuppressWarnings("unchecked")
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull final Capability<T> capability, final @Nullable Direction side) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (LazyOptional<T>) LazyOptional.of(() -> {
                itemHandler.updateFromNBT();
                return itemHandler;
            });
        }
        if (Settings.useEnergy && capability == CapabilityEnergy.ENERGY) {
            return (LazyOptional<T>) LazyOptional.of(() -> {
                energyStorage.updateFromNBT();
                return energyStorage;
            });
        }
        return LazyOptional.empty();
    }
}
