package li.cil.scannable.common.network.message;

import io.netty.buffer.ByteBuf;
import li.cil.scannable.client.scanning.ScanResultProviderStructure;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public final class MessageStructureResponse implements IMessage {
    private ScanResultProviderStructure.StructureLocation[] structures;

    // --------------------------------------------------------------------- //

    public MessageStructureResponse(final ScanResultProviderStructure.StructureLocation[] structures) {
        this.structures = structures;
    }

    @SuppressWarnings("unused") // For deserialization.
    public MessageStructureResponse() {
    }

    public ScanResultProviderStructure.StructureLocation[] getStructures() {
        return structures;
    }

    // --------------------------------------------------------------------- //
    // IMessage

    @Override
    public void fromBytes(final ByteBuf buf) {
        final PacketBuffer packet = new PacketBuffer(buf);
        final int length = packet.readInt();
        structures = new ScanResultProviderStructure.StructureLocation[length];
        for (int i = 0; i < length; i++) {
            final String name = packet.readString(128);
            final BlockPos pos = packet.readBlockPos();
            structures[i] = new ScanResultProviderStructure.StructureLocation(name, pos);
        }
    }

    @Override
    public void toBytes(final ByteBuf buf) {
        final PacketBuffer packet = new PacketBuffer(buf);
        packet.writeInt(structures.length);
        for (final ScanResultProviderStructure.StructureLocation structure : structures) {
            packet.writeString(structure.name);
            packet.writeBlockPos(structure.pos);
        }
    }
}
