package cofh.api.energy;

import net.minecraft.util.EnumFacing;

/**
 * Implement this interface on Tile Entities which should receive energy, generally storing it in one or more internal {@link IEnergyStorage} objects.
 *
 * A reference implementation is provided {@link TileEnergyHandler}.
 *
 * @author King Lemming
 */
public interface IEnergyReceiver extends IEnergyHandler {
    int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate);
}
