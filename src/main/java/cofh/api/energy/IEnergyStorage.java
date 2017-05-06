package cofh.api.energy;

/**
 * An energy storage is the unit of interaction with Energy inventories.<br>
 * This is not to be implemented on TileEntities. This is for internal use only.
 *
 * A reference implementation can be found at {@link EnergyStorage}.
 *
 * @author King Lemming
 */
public interface IEnergyStorage {
    int receiveEnergy(int maxReceive, boolean simulate);

    int extractEnergy(int maxExtract, boolean simulate);

    int getEnergyStored();

    int getMaxEnergyStored();
}
