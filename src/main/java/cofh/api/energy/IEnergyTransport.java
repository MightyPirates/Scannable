package cofh.api.energy;

import net.minecraft.util.EnumFacing;

/**
 * Implement this interface on Tile Entities which transport energy.
 *
 * This is used to "negotiate" connection types between two separate IEnergyTransports, allowing users to set flow direction and allowing for networks Of
 * IEnergyTransports to intelligently transfer energy to other networks.
 */
public interface IEnergyTransport extends IEnergyProvider, IEnergyReceiver {
    enum InterfaceType {
        SEND,
        RECEIVE,
        BALANCE;

        public InterfaceType getOpposite() {
            return this == BALANCE ? BALANCE : this == SEND ? RECEIVE : SEND;
        }

        public InterfaceType rotate() {
            return rotate(true);
        }

        public InterfaceType rotate(boolean forward) {
            if (forward) {
                return this == BALANCE ? RECEIVE : this == RECEIVE ? SEND : BALANCE;
            } else {
                return this == BALANCE ? SEND : this == SEND ? RECEIVE : BALANCE;
            }
        }
    }

    @Override
    int getEnergyStored(EnumFacing from);

    InterfaceType getTransportState(EnumFacing from);

    boolean setTransportState(InterfaceType state, EnumFacing from);
}
