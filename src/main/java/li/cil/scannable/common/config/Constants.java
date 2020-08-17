package li.cil.scannable.common.config;

import li.cil.scannable.api.API;

public final class Constants {
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
    public static final String NAME_MODULE_STRUCTURE = "module_structure";
    public static final String NAME_MODULE_FLUID = "module_fluid";
    public static final String NAME_MODULE_ENTITY = "module_entity";

    // --------------------------------------------------------------------- //
    // Registered scan providers

    public static final String REGISTRY_NAME_SCAN_PROVIDERS = "scan_result_providers";
    public static final String REGISTRY_NAME_SCAN_PROVIDER_BLOCKS = "blocks";
    public static final String REGISTRY_NAME_SCAN_PROVIDER_ENTITIES = "entities";

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
    public static final String CONFIG_ENERGY_MODULE_STRUCTURE = "config.scannable.energyCostModuleStructure";
    public static final String CONFIG_ENERGY_MODULE_FLUID = "config.scannable.energyCostModuleFluid";
    public static final String CONFIG_ENERGY_MODULE_ENTITY = "config.scannable.energyCostModuleEntity";
    public static final String CONFIG_BASE_SCAN_RADIUS = "config.scannable.baseScanRadius";
    public static final String CONFIG_IGNORED_BLOCKS = "config.scannable.ignoredBlocks";
    public static final String CONFIG_IGNORED_BLOCK_TAGS = "config.scannable.ignoredBlockTags";
    public static final String CONFIG_ORE_COMMON_BLOCKS = "config.scannable.oreCommonBlocks";
    public static final String CONFIG_ORE_COMMON_BLOCK_TAGS = "config.scannable.oreCommonBlockTags";
    public static final String CONFIG_ORE_RARE_BLOCKS = "config.scannable.oreRareBlocks";
    public static final String CONFIG_ORE_RARE_BLOCK_TAGS = "config.scannable.oreRareBlockTags";
    public static final String CONFIG_IGNORED_FLUID_TAGS = "config.scannable.ignoredFluidTags";
    public static final String CONFIG_STRUCTURES = "config.scannable.structures";
    public static final String CONFIG_BLOCK_COLORS = "config.scannable.blockColors";
    public static final String CONFIG_BLOCK_TAG_COLORS = "config.scannable.blockTagColors";
    public static final String CONFIG_FLUID_COLORS = "config.scannable.fluidColors";
    public static final String CONFIG_FLUID_TAG_COLORS = "config.scannable.fluidTagColors";

    // --------------------------------------------------------------------- //
    // GUI labels

    public static final String GUI_SCANNER_TITLE = "gui.scannable.scanner.title";
    public static final String GUI_SCANNER_MODULES = "gui.scannable.scanner.modules";
    public static final String GUI_SCANNER_MODULES_TOOLTIP = "gui.scannable.scanner.modules.tooltip";
    public static final String GUI_SCANNER_MODULES_INACTIVE = "gui.scannable.scanner.modules_inactive";
    public static final String GUI_SCANNER_MODULES_INACTIVE_TOOLTIP = "gui.scannable.scanner.modules_inactive.tooltip";
    public static final String GUI_SCANNER_PROGRESS = "gui.scannable.scanner.progress";
    public static final String GUI_MODULE_BLOCK_LIST = "gui.scannable.module_block.list";
    public static final String GUI_MODULE_ENTITY_LIST = "gui.scannable.module_entity.list";
    public static final String GUI_OVERLAY_LABEL_DISTANCE = "gui.scannable.overlay.entity_details";

    // --------------------------------------------------------------------- //
    // Chat messages

    public static final int CHAT_LINE_ID = 1000000000 + API.MOD_ID.hashCode() % 1000000000; // This should make collisions unlikely enough, right? Right?!
    public static final String MESSAGE_NO_SCAN_MODULES = "message.scannable.no_scan_modules";
    public static final String MESSAGE_NOT_ENOUGH_ENERGY = "message.scannable.not_enough_energy";
    public static final String MESSAGE_BLOCK_BLACKLISTED = "message.scannable.block_blacklisted";
    public static final String MESSAGE_NO_FREE_SLOTS = "message.scannable.no_free_slots";

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
    public static final String TOOLTIP_MODULE_BLOCK_LIST = "tooltip.scannable.module_block.list";
    public static final String TOOLTIP_MODULE_STRUCTURE = "tooltip.scannable.module_structure";
    public static final String TOOLTIP_MODULE_STRUCTURE_SHOW_EXPLORED = "tooltip.scannable.module_structure.show_explored";
    public static final String TOOLTIP_MODULE_STRUCTURE_HIDE_EXPLORED = "tooltip.scannable.module_structure.hide_explored";
    public static final String TOOLTIP_MODULE_FLUID = "tooltip.scannable.module_fluid";
    public static final String TOOLTIP_MODULE_ENTITY = "tooltip.scannable.module_entity";
    public static final String TOOLTIP_MODULE_ENTITY_LIST = "tooltip.scannable.module_entity.list";
    public static final String TOOLTIP_LIST_ITEM_FORMAT = "tooltip.scannable.list_item";

    // --------------------------------------------------------------------- //
    // Scanner settings

    // The number of ticks over which to compute scan results. Which is at the
    // same time the use time of the scanner item.
    public static final int SCAN_COMPUTE_DURATION = 40;
    // Initial radius of the scan wave.
    public static final int SCAN_INITIAL_RADIUS = 10;
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

    // Number of modules that can be active in a scanner.
    public static final int SCANNER_ACTIVE_MODULE_COUNT = 3;
    // Number of additional modules that can be stored in a scanner.
    public static final int SCANNER_INACTIVE_MODULE_COUNT = 6;
    // Total number of modules that can be placed in a scanner.
    public static final int SCANNER_TOTAL_MODULE_COUNT = SCANNER_ACTIVE_MODULE_COUNT + SCANNER_INACTIVE_MODULE_COUNT;
    // By how much to scale the base scan range when scanning for ores.
    public static final float MODULE_ORE_RADIUS_MULTIPLIER = 0.25f;
    // By how much to scale the base scan range when scanning for specific blocks.
    public static final float MODULE_BLOCK_RADIUS_MULTIPLIER = 0.5f;
    // By how much to scale the base scan range when scanning for structures.
    public static final float MODULE_STRUCTURE_RADIUS_MULTIPLIER = 2.0f;

    // Number of slots we have in configurable modules (block and entity modules).
    public static final int CONFIGURABLE_MODULE_SLOTS = 5;
}
