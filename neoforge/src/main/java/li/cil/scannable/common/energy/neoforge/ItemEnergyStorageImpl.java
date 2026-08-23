/* SPDX-License-Identifier: MIT */

package li.cil.scannable.common.energy.neoforge;

import li.cil.scannable.common.config.ServerConfig;
import li.cil.scannable.common.energy.ItemEnergyStorage;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;

import java.util.Optional;

public final class ItemEnergyStorageImpl {
    public static Optional<ItemEnergyStorage> of(final ItemStack container) {
        if (!ServerConfig.useEnergy) {
            return Optional.empty();
        }

        return Optional.ofNullable(container.getCapability(Capabilities.EnergyStorage.ITEM)).map(capability -> new ItemEnergyStorage() {
            @Override
            public long receiveEnergy(final long amount, final boolean simulate) {
                final int clampedAmount = (int) Math.min(amount, Integer.MAX_VALUE);
                return capability.receiveEnergy(clampedAmount, simulate);
            }

            @Override
            public long extractEnergy(final long amount, final boolean simulate) {
                final int clampedAmount = (int) Math.min(amount, Integer.MAX_VALUE);
                return capability.extractEnergy(clampedAmount, simulate);
            }

            @Override
            public long getEnergyStored() {
                return capability.getEnergyStored();
            }

            @Override
            public long getMaxEnergyStored() {
                return capability.getMaxEnergyStored();
            }
        });
    }
}
