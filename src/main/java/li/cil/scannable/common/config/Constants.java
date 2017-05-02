package li.cil.scannable.common.config;

public final class Constants {
    // --------------------------------------------------------------------- //
    // Mod data

    public static final String MOD_NAME = "Scannable";
    public static final String PROXY_CLIENT = "li.cil.scannable.client.ProxyClient";
    public static final String PROXY_SERVER = "li.cil.scannable.server.ProxyServer";

    // --------------------------------------------------------------------- //
    // Block, item, entity and container names

    public static final String NAME_SCANNER = "scanner";
    public static final String NAME_MODULE_BLANK = "module_blank";
    public static final String NAME_MODULE_RANGE = "module_range";
    public static final String NAME_MODULE_ANIMAL = "module_animal";
    public static final String NAME_MODULE_MONSTER = "module_monster";
    public static final String NAME_MODULE_ORE_COMMON = "module_ore_common";
    public static final String NAME_MODULE_ORE_RARE = "module_ore_rare";

    // --------------------------------------------------------------------- //
    // Config

    public static final String CONFIG_USE_ENERGY = "config.scannable.useEnergy";
    public static final String CONFIG_ORE_BLACKLIST = "config.scannable.oreBlacklist";
    public static final String CONFIG_ORE_COLORS = "config.scannable.oreColors";
    public static final String CONFIG_ORES_COMMON = "config.scannable.oresCommon";
    public static final String CONFIG_ORES_RARE = "config.scannable.oresRare";

    // --------------------------------------------------------------------- //
    // GUI labels

    public static final String GUI_SCANNER_TITLE = "gui.scannable.scanner.title";
    public static final String GUI_SCANNER_MODULES = "gui.scannable.scanner.modules";

    // --------------------------------------------------------------------- //
    // Tooltips

    public static final String TOOLTIP_SCANNER = "tooltip.scannable.scanner";

    // --------------------------------------------------------------------- //
    // Scanner settings

    // The radius in which to collect scan results around the player.
    public static final float SCAN_RADIUS = 64f;
    // The number of ticks over which to compute scan results. Which is at the
    // same time the use time of the scanner item.
    public static final int SCAN_COMPUTE_DURATION = 40;
    // How long the ping takes to reach the end of the visible area at the
    // default visibility range of 12 chunks.
    public static final long SCAN_GROWTH_DURATION = 2000;
    // How long the results from a scan should remain visible.
    public static final long SCAN_STAY_DURATION = 10000;

    // By how much the scan radius is increased by installing the range module.
    public static final float MODULE_RANGE_RADIUS_INCREASE = 32;
    // By how much to scale the base scan range when scanning for ores.
    public static final float MODULE_ORE_RADIUS_MULTIPLIER = 0.5f;
}
