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
    public static final String TOOLTIP_SCANNER_ENERGY = "tooltip.scannable.scanner.energy";

    // --------------------------------------------------------------------- //
    // Scanner settings

    // The radius in which to collect scan results around the player.
    public static final float SCAN_RADIUS = 64f;
    // The number of ticks over which to compute scan results. Which is at the
    // same time the use time of the scanner item.
    public static final int SCAN_COMPUTE_DURATION = 40;
    // Initial radius of the scan wave.
    public static final int SCAN_INITIAL_RADIUS = 12;
    // Scan wave growth time offset to avoid super slow start speed.
    public static final int SCAN_TIME_OFFSET = 200;
    // How long the ping takes to reach the end of the visible area at the
    // default visibility range of 12 chunks.
    public static final int SCAN_GROWTH_DURATION = 2000;
    // How long the results from a scan should remain visible.
    public static final int SCAN_STAY_DURATION = 10000;

    // Reference render distance the above constants are relative to.
    public static final int REFERENCE_RENDER_DISTANCE = 12;
    // Size of a chunk. Duh.
    public static final int CHUNK_SIZE = 16;

    // Amount of energy that can be stored in a scanner.
    public static final int SCANNER_ENERGY_CAPACITY = 5000;
    // Amount of energy consumed per scan.
    public static final int SCANNER_ENERGY_COST = 100;
    // Number of modules that can be installed in a scanner.
    public static final int SCANNER_MAX_MODULE_COUNT = 3;
    // By how much the scan radius is increased by installing the range module.
    public static final float MODULE_RANGE_RADIUS_INCREASE = 32;
    // By how much to scale the base scan range when scanning for ores.
    public static final float MODULE_ORE_RADIUS_MULTIPLIER = 0.5f;
}
