package li.cil.scannable.common.config;

import net.minecraft.network.PacketBuffer;

// Mirror of Settings, but with authoritative server settings.
public final class ServerSettings {
    final boolean useEnergy;
    final int energyCapacityScanner;
    final int energyCostModuleRange;
    final int energyCostModuleAnimal;
    final int energyCostModuleMonster;
    final int energyCostModuleOreCommon;
    final int energyCostModuleOreRare;
    final int energyCostModuleBlock;
    final int energyCostModuleFluid;
    final int energyCostModuleEntity;
    final int baseScanRadius;
    final String[] blockBlacklist;
    final String[] oresBlacklist;
    final String[] oresCommon;
    final String[] oresRare;
    final String[] fluidBlacklist;

    public ServerSettings(final PacketBuffer packet) {
        useEnergy = packet.readBoolean();
        energyCapacityScanner = packet.readInt();
        energyCostModuleRange = packet.readInt();
        energyCostModuleAnimal = packet.readInt();
        energyCostModuleMonster = packet.readInt();
        energyCostModuleOreCommon = packet.readInt();
        energyCostModuleOreRare = packet.readInt();
        energyCostModuleBlock = packet.readInt();
        energyCostModuleFluid = packet.readInt();
        energyCostModuleEntity = packet.readInt();
        baseScanRadius = packet.readInt();
        blockBlacklist = readStringArray(packet);
        oresBlacklist = readStringArray(packet);
        oresCommon = readStringArray(packet);
        oresRare = readStringArray(packet);
        fluidBlacklist = readStringArray(packet);
    }

    public ServerSettings() {
        useEnergy = Settings.useEnergy;
        energyCapacityScanner = Settings.energyCapacityScanner;
        energyCostModuleRange = Settings.energyCostModuleRange;
        energyCostModuleAnimal = Settings.energyCostModuleAnimal;
        energyCostModuleMonster = Settings.energyCostModuleMonster;
        energyCostModuleOreCommon = Settings.energyCostModuleOreCommon;
        energyCostModuleOreRare = Settings.energyCostModuleOreRare;
        energyCostModuleBlock = Settings.energyCostModuleBlock;
        energyCostModuleFluid = Settings.energyCostModuleFluid;
        energyCostModuleEntity = Settings.energyCostModuleEntity;
        baseScanRadius = Settings.baseScanRadius;
        blockBlacklist = Settings.blockBlacklist;
        oresBlacklist = Settings.oreBlacklist;
        oresCommon = Settings.oresCommon;
        oresRare = Settings.oresRare;
        fluidBlacklist = Settings.fluidBlacklist;
    }

    public void writeToBuffer(final PacketBuffer packet) {
        packet.writeBoolean(useEnergy);
        packet.writeInt(energyCapacityScanner);
        packet.writeInt(energyCostModuleRange);
        packet.writeInt(energyCostModuleAnimal);
        packet.writeInt(energyCostModuleMonster);
        packet.writeInt(energyCostModuleOreCommon);
        packet.writeInt(energyCostModuleOreRare);
        packet.writeInt(energyCostModuleBlock);
        packet.writeInt(energyCostModuleFluid);
        packet.writeInt(energyCostModuleEntity);
        packet.writeInt(baseScanRadius);
        writeStringArray(packet, blockBlacklist);
        writeStringArray(packet, oresBlacklist);
        writeStringArray(packet, oresCommon);
        writeStringArray(packet, oresRare);
        writeStringArray(packet, fluidBlacklist);
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
