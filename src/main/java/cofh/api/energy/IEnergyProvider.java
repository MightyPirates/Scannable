package cofh.api.energy;

import net.minecraft.util.EnumFacing;

/**
 * Implement this interface on Tile Entities which should provide energy, generally storing it in one or more internal {@link IEnergyStorage} objects.
 *
 * A reference implementation is provided {@link TileEnergyHandler}.
 *
 * @author King Lemming
 */
public interface IEnergyProvider extends IEnergyHandler {
    int extractEnergy(EnumFacing from, int maxExtract, boolean simulate);
}
