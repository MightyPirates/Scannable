package li.cil.scannable.common.config;

public final class Constants {
    // --------------------------------------------------------------------- //
    // Tooltips

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
