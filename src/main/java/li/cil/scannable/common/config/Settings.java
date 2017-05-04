package li.cil.scannable.common.config;

import li.cil.scannable.api.API;
import net.minecraftforge.common.config.Config;

@Config(modid = API.MOD_ID)
public final class Settings {
    @Config.LangKey(Constants.CONFIG_USE_ENERGY)
    @Config.Comment("Whether to consume energy when performing a scan.\n" +
                    "Will make the scanner a chargeable item.")
    public static boolean useEnergy;

    @Config.LangKey(Constants.CONFIG_ORE_BLACKLIST)
    @Config.Comment("Ore dictionary entries that match the common ore pattern but should be ignored.")
    public static String[] oresBlacklist = {
    };

    @Config.LangKey(Constants.CONFIG_ORE_COLORS)
    @Config.Comment("The colors for ores used when rendering their result bounding box.\n" +
                    "Each entry must be a key-value pair separated by a `=`, with the.\n" +
                    "key being the ore dictionary name and the value being the hexadecimal\n" +
                    "RGB value of the color.")
    public static String[] oreColors = {
            "oreCoal=0x666666",
            "oreIron=0xCCCC99",
            "oreGold=0xFFFF66",
            "oreLapis=0x3399FF",
            "oreDiamond=0x99FFFF",
            "oreRedstone=0xFF6633",
            "oreEmerald=0x33CC33",
            "oreQuartz=0xCCCCCC"
    };

    @Config.LangKey(Constants.CONFIG_ORES_COMMON)
    @Config.Comment("Ore dictionary entries considered common ores.\n" +
                    "Anything matching /ore[A-Z].*/ that isn't in the rare ore list is\n" +
                    "automatically considered a common ore.")
    public static String[] oresCommon = {
            "glowstone"
    };

    @Config.LangKey(Constants.CONFIG_ORES_RARE)
    @Config.Comment("Ore dictionary names of ores considered 'rare', requiring the rare ore scanner module.")
    public static String[] oresRare = {
            "oreGold",
            "oreDiamond",
            "oreLapis",
            "oreEmerald",

            // Thermal Foundation
            "oreSilver",
            "oreNickel",
            "orePlatinum",
            "oreMithril",
            "oreUranium",

            // Tinker's Construct
            "oreArdite",
            "oreCobalt",

            // Thaumcraft
            "oreInfusedAir",
            "oreInfusedFire",
            "oreInfusedWater",
            "oreInfusedEarth",
            "oreInfusedOrder",
            "oreInfusedEntropy"
    };

    // --------------------------------------------------------------------- //

    private Settings() {
    }
}
