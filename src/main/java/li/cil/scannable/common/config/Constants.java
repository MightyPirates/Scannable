package li.cil.scannable.common.config;

import li.cil.scannable.api.API;

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
    public static final String NAME_MODULE_BLOCK = "module_block";

    // --------------------------------------------------------------------- //
    // Config

    public static final String CONFIG_USE_ENERGY = "config.scannable.useEnergy";
    public static final String CONFIG_ENERGY_CAPACITY_SCANNER = "config.scannable.energyCapacityScanner";
    public static final String CONFIG_ENERGY_MODULE_RANGE = "config.scannable.energyCostModuleRange";
    public static final String CONFIG_ENERGY_MODULE_ANIMAL = "config.scannable.energyCostModuleAnimal";
    public static final String CONFIG_ENERGY_MODULE_MONSTER = "config.scannable.energyCostModuleMonster";
    public static final String CONFIG_ENERGY_MODULE_ORE_COMMON = "config.scannable.energyCostModuleOreCommon";
    public static final String CONFIG_ENERGY_MODULE_ORE_RARE = "config.scannable.energyCostModuleOreRare";
    public static final String CONFIG_ENERGY_MODULE_BLOCK = "config.scannable.energyCostModuleBlock";
    public static final String CONFIG_BASE_SCAN_RADIUS = "config.scannable.baseScanRadius";
    public static final String CONFIG_BLOCK_BLACKLIST = "config.scannable.blockBlacklist";
    public static final String CONFIG_ORE_BLACKLIST = "config.scannable.oreBlacklist";
    public static final String CONFIG_ORE_COLORS = "config.scannable.oreColors";
    public static final String CONFIG_ORES_COMMON = "config.scannable.oresCommon";
    public static final String CONFIG_ORES_RARE = "config.scannable.oresRare";

    // --------------------------------------------------------------------- //
    // GUI labels

    public static final String GUI_SCANNER_TITLE = "gui.scannable.scanner.title";
    public static final String GUI_SCANNER_MODULES = "gui.scannable.scanner.modules";
    public static final String GUI_SCANNER_PROGRESS = "gui.scannable.scanner.progress";
    public static final String GUI_OVERLAY_LABEL_DISTANCE = "gui.scannable.overlay.entity_details";

    // --------------------------------------------------------------------- //
    // Chat messages

    public static final int CHAT_LINE_ID = 1000000000 + API.MOD_ID.hashCode() % 1000000000; // This should make collisions unlikely enough, right? Right?!
    public static final String MESSAGE_NO_SCAN_MODULES = "message.scannable.no_scan_modules";
    public static final String MESSAGE_NOT_ENOUGH_ENERGY = "message.scannable.not_enough_energy";
    public static final String MESSAGE_BLOCK_BLACKLISTED = "message.scannable.block_blacklisted";

    // --------------------------------------------------------------------- //
    // Tooltips

    public static final String TOOLTIP_SCANNER = "tooltip.scannable.scanner";
    public static final String TOOLTIP_SCANNER_ENERGY = "tooltip.scannable.scanner.energy";
    public static final String TOOLTIP_MODULE_ENERGY_COST = "tooltip.scannable.module.energy_cost";
    public static final String TOOLTIP_MODULE_RANGE = "tooltip.scannable.module_range";
    public static final String TOOLTIP_MODULE_ANIMAL = "tooltip.scannable.module_animal";
    public static final String TOOLTIP_MODULE_MONSTER = "tooltip.scannable.module_monster";
    public static final String TOOLTIP_MODULE_ORE_COMMON = "tooltip.scannable.module_ore_common";
    public static final String TOOLTIP_MODULE_ORE_RARE = "tooltip.scannable.module_ore_rare";
    public static final String TOOLTIP_MODULE_BLOCK = "tooltip.scannable.module_block";
    public static final String TOOLTIP_MODULE_BLOCK_NAME = "tooltip.scannable.module_block.name";

    // --------------------------------------------------------------------- //
    // Scanner settings

    // The number of ticks over which to compute scan results. Which is at the
    // same time the use time of the scanner item.
    public static final int SCAN_COMPUTE_DURATION = 40;
    // Initial radius of the scan wave.
    public static final int SCAN_INITIAL_RADIUS = 8;
    // Scan wave growth time offset to avoid super slow start speed.
    public static final int SCAN_TIME_OFFSET = 200;
    // How long the ping takes to reach the end of the visible area.
    public static final int SCAN_GROWTH_DURATION = 2000;
    // How long the results from a scan should remain visible.
    public static final int SCAN_STAY_DURATION = 10000;

    // Reference render distance the above constants are relative to.
    public static final int REFERENCE_RENDER_DISTANCE = 12;
    // Size of a chunk. Duh.
    public static final int CHUNK_SIZE = 16;

    // Number of modules that can be installed in a scanner.
    public static final int SCANNER_MAX_MODULE_COUNT = 3;
    // By how much to scale the base scan range when scanning for ores.
    public static final float MODULE_ORE_RADIUS_MULTIPLIER = 0.25f;
    // By how much to scale the base scan range when scanning for specific blocks.
    public static final float MODULE_BLOCK_RADIUS_MULTIPLIER = 0.5f;
}
