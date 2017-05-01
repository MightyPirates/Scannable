package li.cil.scannable.common.config;

import li.cil.scannable.api.API;
import net.minecraftforge.common.config.Config;

@Config(modid = API.MOD_ID)
public final class Settings {
    @Config.LangKey(Constants.CONFIG_USE_ENERGY)
    @Config.Comment("Whether to consume energy when performing a scan. Will make the scanner a chargeable item.")
    @Config.RequiresWorldRestart
    public static boolean useEnergy;

    @Config.LangKey(Constants.CONFIG_ORES_RARE)
    @Config.Comment("Ore dictionary names of ores considered 'rare', requiring the rare ore scanner module.")
    public static String[] oresRare = {
            "oreGold",
            "oreDiamond",
            "oreLapis",
            "oreEmerald"
    };

    @Config.LangKey(Constants.CONFIG_ORES_COMMON)
    @Config.Comment("Ore dictionary entries considered common ores. Anything matching /ore[A-Z].*/ that isn't in the rare ore list is automatically considered a common ore.")
    public static String[] oresCommon = {
    };

    @Config.LangKey(Constants.CONFIG_ORES_BLACKLIST)
    @Config.Comment("Ore dictionary entries that match the common ore pattern but should be ignored.")
    public static String[] oresBlacklist = {
    };

    // --------------------------------------------------------------------- //

    private Settings() {
    }
}
