package li.cil.scannable.common.energy.forge;

import li.cil.scannable.common.energy.EnergyStorage;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import java.util.Optional;

public class EnergyStorageImpl {
    public static Optional<EnergyStorage> of(final ItemStack stack) {
        return stack.getCapability(ForgeCapabilities.ENERGY).map(capability -> new EnergyStorage() {
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
