package li.cil.scannable.common.config;

import li.cil.scannable.api.API;
import li.cil.scannable.client.scanning.ScanResultProviderOre;
import net.minecraftforge.common.config.Config;

import javax.annotation.Nullable;

@Config(modid = API.MOD_ID)
public final class Settings {
    @Config.LangKey(Constants.CONFIG_USE_ENERGY)
    @Config.Comment("Whether to consume energy when performing a scan.\n" +
                    "Will make the scanner a chargeable item.")
    @Config.RequiresWorldRestart
    public static boolean useEnergy;

    @Config.LangKey(Constants.CONFIG_ORE_BLACKLIST)
    @Config.Comment("Ore dictionary entries that match the common ore pattern but should be ignored.")
    @Config.RequiresWorldRestart
    public static String[] oreBlacklist = {
    };

    @Config.LangKey(Constants.CONFIG_ORES_COMMON)
    @Config.Comment("Ore dictionary entries considered common ores, requiring the common ore scanner module.\n" +
                    "Use this to mark ores as common, as opposed to rare (see oresRare).")
    @Config.RequiresWorldRestart
    public static String[] oresCommon = {
            // Minecraft
            "oreCoal",
            "oreIron",
            "oreRedstone",
            "glowstone",

            // Thermal Foundation
            "oreCopper",
            "oreTin",
            "oreLead",

            // Immersive Engineering
            "oreAluminum",
            "oreAluminium",

            // Thaumcraft
            "oreCinnabar"
    };

    @Config.LangKey(Constants.CONFIG_ORES_RARE)
    @Config.Comment("Ore dictionary names of ores considered 'rare', requiring the rare ore scanner module.\n" +
                    "Anything matching /ore[A-Z].*/ that isn't in the common ore list is\n" +
                    "automatically considered a rare ore (as opposed to the other way around,\n" +
                    "to make missing entries less likely be a problem). Use this to add rare\n" +
                    "ores that do follow this pattern.")
    @Config.RequiresWorldRestart
    public static String[] oresRare = {
    };

    @Config.LangKey(Constants.CONFIG_ORE_COLORS)
    @Config.Comment("The colors for ores used when rendering their result bounding box.\n" +
                    "Each entry must be a key-value pair separated by a `=`, with the.\n" +
                    "key being the ore dictionary name and the value being the hexadecimal\n" +
                    "RGB value of the color.")
    public static String[] oreColors = {
            // Minecraft
            "oreCoal=0x635F5C",
            "oreIron=0xA17951",
            "oreGold=0xF4F71F",
            "oreLapis=0x4863F0",
            "oreDiamond=0x48E2F0",
            "oreRedstone=0xE61E1E",
            "oreEmerald=0x12BA16",
            "oreQuartz=0xB3D9D2",
            "glowstone=0xE9E68E",

            // Thermal Foundation
            "oreCopper=0xE4A020",
            "oreLead=0x8187C3",
            "oreMithril=0x97D5FE",
            "oreNickel=0xD0D3AC",
            "orePlatinum=0x7AC0FD",
            "oreSilver=0xE8F2FB",
            "oreTin=0xCCE4FE",

            // Misc.
            "oreAluminum=0xCBE4E2",
            "oreAluminium=0xCBE4E2",
            "orePlutonium=0x9DE054",
            "oreUranium=0x9DE054",
            "oreYellorium=0xD8E054",

            // Tinker's Construct
            "oreArdite=0xB77E11",
            "oreCobalt=0x413BB8",

            // Thaumcraft
            "oreCinnabar=0xF5DA25",
            "oreInfusedAir=0xF7E677",
            "oreInfusedFire=0xDC7248",
            "oreInfusedWater=0x9595D5",
            "oreInfusedEarth=0x49B45A",
            "oreInfusedOrder=0x9FF2DE",
            "oreInfusedEntropy=0x545476"
    };

    // --------------------------------------------------------------------- //

    private static ServerSettings serverSettings;

    public static void setServerSettings(@Nullable final ServerSettings serverSettings) {
        Settings.serverSettings = serverSettings;
        ScanResultProviderOre.INSTANCE.rebuildOreCache();
    }

    // --------------------------------------------------------------------- //

    public static boolean useEnergy() {
        return serverSettings != null ? serverSettings.useEnergy : useEnergy;
    }

    public static String[] getOreBlacklist() {
        return serverSettings != null ? serverSettings.oresBlacklist : oreBlacklist;
    }

    public static String[] getCommonOres() {
        return serverSettings != null ? serverSettings.oresCommon : oresCommon;
    }

    public static String[] getRareOres() {
        return serverSettings != null ? serverSettings.oresRare : oresRare;
    }

    public static String[] getOreColors() {
        return oreColors;
    }

    // --------------------------------------------------------------------- //

    private Settings() {
    }
}
