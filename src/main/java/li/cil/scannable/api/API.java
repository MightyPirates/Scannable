package li.cil.scannable.api;

/**
 * Glue / actual references for the Scannable API.
 */
public final class API {
    /**
     * The ID of the mod, i.e. the internal string it is identified by.
     */
    public static final String MOD_ID = "scannable";

    /**
     * The current version of the mod.
     */
    public static final String MOD_VERSION = "@VERSION@";

    // --------------------------------------------------------------------- //

    // Set in pre-init, prefer using static entry point classes instead.
    public static li.cil.scannable.api.detail.ScanningAPI scanningAPI;
}
