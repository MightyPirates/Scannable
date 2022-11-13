package li.cil.scannable.common.capabilities;

import li.cil.scannable.common.config.CommonConfig;
import li.cil.scannable.common.energy.ScannerEnergyStorage;
import li.cil.scannable.common.inventory.ScannerItemHandler;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ScannerWrapper implements ICapabilityProvider {
    private final ScannerItemHandler itemHandler;
    private final ScannerEnergyStorage energyStorage;

    private final LazyOptional<IItemHandler> itemHandlerHolder;
    private final LazyOptional<IEnergyStorage> energyStorageHolder;

    // --------------------------------------------------------------------- //

    public ScannerWrapper(final ItemStack container) {
        itemHandler = new ScannerItemHandler(container);
        energyStorage = new ScannerEnergyStorage(container);

        itemHandlerHolder = LazyOptional.of(() -> {
            itemHandler.updateFromNBT();
            return itemHandler;
        });
        energyStorageHolder = LazyOptional.of(() -> {
            energyStorage.updateFromNBT();
            return energyStorage;
        });
    }

    // --------------------------------------------------------------------- //
    // ICapabilityProvider

    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull final Capability<T> capability, final @Nullable Direction side) {
        final LazyOptional<T> itemHandlerCapability = CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(capability, itemHandlerHolder);
        if (itemHandlerCapability.isPresent()) {
            return itemHandlerCapability;
        }

        final LazyOptional<T> energyCapability = CapabilityEnergy.ENERGY.orEmpty(capability, energyStorageHolder);
        if (CommonConfig.useEnergy && energyCapability.isPresent()) {
            return energyCapability;
        }
        return LazyOptional.empty();
    }
}
