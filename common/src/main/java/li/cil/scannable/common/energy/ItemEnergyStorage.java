package li.cil.scannable.common.energy;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Contract;

import java.util.Optional;

@SuppressWarnings("Contract")
public interface ItemEnergyStorage {
    @ExpectPlatform
    @Contract("_ -> !null")
    static Optional<ItemEnergyStorage> of(final ItemStack stack) {
        throw new AssertionError();
    }

    long receiveEnergy(long amount, boolean simulate);

    long extractEnergy(long amount, boolean simulate);

    long getEnergyStored();

    long getMaxEnergyStored();
}
