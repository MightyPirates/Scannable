/* SPDX-License-Identifier: MIT */

package li.cil.scannable.common.energy.neoforge;

import li.cil.scannable.common.config.ServerConfig;
import li.cil.scannable.common.item.ModDataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.energy.EnergyStorage;

public final class ScannerEnergyStorage extends EnergyStorage {
    private final ItemStack container;

    public ScannerEnergyStorage(final ItemStack container) {
        super(ServerConfig.energyCapacityScanner);
        this.container = container;

        this.energy = Mth.clamp(container.getOrDefault(ModDataComponents.ENERGY.get(), 0), 0, this.capacity);
    }

    // --------------------------------------------------------------------- //
    // IEnergyStorage

    @Override
    public int receiveEnergy(final int maxReceive, final boolean simulate) {
        final int energyReceived = super.receiveEnergy(maxReceive, simulate);
        if (!simulate && energyReceived != 0) {
            container.set(ModDataComponents.ENERGY.get(), energy);
        }

        return energyReceived;
    }

    @Override
    public int extractEnergy(final int maxExtract, final boolean simulate) {
        final int energyExtracted = super.extractEnergy(maxExtract, simulate);
        if (!simulate && energyExtracted != 0) {
            container.set(ModDataComponents.ENERGY.get(), energy);
        }

        return energyExtracted;
    }
}
