package cofh.api.energy;

import net.minecraft.util.EnumFacing;

/**
 * Implement this interface on Tile Entities which should handle energy, generally storing it in one or more internal {@link IEnergyStorage} objects.
 *
 * A reference implementation is provided {@link TileEnergyHandler}.
 *
 * Note that {@link IEnergyReceiver} and {@link IEnergyProvider} are extensions of this.
 *
 * @author King Lemming
 */
public interface IEnergyHandler extends IEnergyConnection {
	int getEnergyStored(EnumFacing from);

	int getMaxEnergyStored(EnumFacing from);
}
