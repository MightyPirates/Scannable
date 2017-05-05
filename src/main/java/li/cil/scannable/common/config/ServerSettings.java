package li.cil.scannable.common.config;

import net.minecraft.network.PacketBuffer;

// Mirror of Settings, but with authoritative server settings.
public final class ServerSettings {
    public final boolean useEnergy;
    public final String[] blockBlacklist;
    public final String[] oresBlacklist;
    public final String[] oresCommon;
    public final String[] oresRare;

    public ServerSettings(final PacketBuffer packet) {
        useEnergy = packet.readBoolean();
        blockBlacklist = readStringArray(packet);
        oresBlacklist = readStringArray(packet);
        oresCommon = readStringArray(packet);
        oresRare = readStringArray(packet);
    }

    public ServerSettings() {
        useEnergy = Settings.useEnergy;
        blockBlacklist = Settings.blockBlacklist;
        oresBlacklist = Settings.oreBlacklist;
        oresCommon = Settings.oresCommon;
        oresRare = Settings.oresRare;
    }

    public void writeToBuffer(final PacketBuffer packet) {
        packet.writeBoolean(useEnergy);
        writeStringArray(packet, blockBlacklist);
        writeStringArray(packet, oresBlacklist);
        writeStringArray(packet, oresCommon);
        writeStringArray(packet, oresRare);
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
