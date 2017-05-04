package li.cil.scannable.common.network.message;

import io.netty.buffer.ByteBuf;
import li.cil.scannable.common.config.ServerSettings;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public final class MessageConfig implements IMessage {
    private ServerSettings settings;

    // --------------------------------------------------------------------- //

    public MessageConfig() {
        settings = new ServerSettings();
    }

    public ServerSettings getSettings() {
        return settings;
    }

    // --------------------------------------------------------------------- //
    // IMessage

    @Override
    public void fromBytes(final ByteBuf buf) {
        final PacketBuffer packet = new PacketBuffer(buf);
        final boolean useEnergy = packet.readBoolean();
        final String[] oresBlacklist = readStringArray(packet);
        final String[] oresCommon = readStringArray(packet);
        final String[] oresRare = readStringArray(packet);

        settings = new ServerSettings(useEnergy, oresBlacklist, oresCommon, oresRare);
    }

    @Override
    public void toBytes(final ByteBuf buf) {
        final PacketBuffer packet = new PacketBuffer(buf);
        packet.writeBoolean(settings.useEnergy);
        writeStringArray(packet, settings.oresBlacklist);
        writeStringArray(packet, settings.oresCommon);
        writeStringArray(packet, settings.oresRare);
    }

    // --------------------------------------------------------------------- //

    private String[] readStringArray(final PacketBuffer packet) {
        final int length = packet.readInt();
        final String[] array = new String[length];
        for (int i = 0; i < length; i++) {
            array[i] = packet.readString(256);
        }
        return array;
    }

    private static void writeStringArray(final PacketBuffer buffer, final String[] array) {
        buffer.writeInt(array.length);
        for (final String element : array) {
            buffer.writeString(element);
        }
    }
}
