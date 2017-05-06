package cofh.api.energy;

import net.minecraft.item.ItemStack;

/**
 * Implement this interface on Item classes that support external manipulation of their internal energy storages.
 *
 * A reference implementation is provided {@link ItemEnergyContainer}.
 *
 * @author King Lemming
 */
public interface IEnergyContainerItem {
	int receiveEnergy(ItemStack container, int maxReceive, boolean simulate);

	int extractEnergy(ItemStack container, int maxExtract, boolean simulate);

	int getEnergyStored(ItemStack container);

	int getMaxEnergyStored(ItemStack container);
}
