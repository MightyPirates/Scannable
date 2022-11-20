package li.cil.scannable.common.energy;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public interface ItemEnergyStorage {
    @ExpectPlatform
    static Optional<ItemEnergyStorage> of(final ItemStack stack) {
        throw new AssertionError();
    }

    long receiveEnergy(long amount, boolean simulate);

    long extractEnergy(long amount, boolean simulate);

    long getEnergyStored();

    long getMaxEnergyStored();
}
