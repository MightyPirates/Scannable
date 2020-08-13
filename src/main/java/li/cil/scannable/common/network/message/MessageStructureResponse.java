package li.cil.scannable.common.network.message;

import io.netty.buffer.ByteBuf;
import li.cil.scannable.client.scanning.ScanResultProviderStructure;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public final class MessageStructureResponse {
    private ScanResultProviderStructure.StructureLocation[] structures;

    // --------------------------------------------------------------------- //

    public MessageStructureResponse(final ScanResultProviderStructure.StructureLocation[] structures) {
        this.structures = structures;
    }

    public MessageStructureResponse(final ByteBuf buffer) {
        fromBytes(buffer);
    }

    // --------------------------------------------------------------------- //

    public static boolean handle(final MessageStructureResponse message, final Supplier<NetworkEvent.Context> context) {
        ScanResultProviderStructure.INSTANCE.setStructures(message.structures);

        return true;
    }

    // --------------------------------------------------------------------- //

    public void fromBytes(final ByteBuf buffer) {
        final PacketBuffer packet = new PacketBuffer(buffer);
        final int length = packet.readInt();
        structures = new ScanResultProviderStructure.StructureLocation[length];
        for (int i = 0; i < length; i++) {
            final ITextComponent name = packet.readTextComponent();
            final BlockPos pos = packet.readBlockPos();
            structures[i] = new ScanResultProviderStructure.StructureLocation(name, pos);
        }
    }

    public void toBytes(final ByteBuf buffer) {
        final PacketBuffer packet = new PacketBuffer(buffer);
        packet.writeInt(structures.length);
        for (final ScanResultProviderStructure.StructureLocation structure : structures) {
            packet.writeTextComponent(structure.name);
            packet.writeBlockPos(structure.pos);
        }
    }
}
