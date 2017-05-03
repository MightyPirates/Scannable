package li.cil.scannable.api;

import net.minecraft.creativetab.CreativeTabs;

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

    // The creative tab holding all the good stuff.
    public static CreativeTabs creativeTab;

    // --------------------------------------------------------------------- //

    private API() {
    }
}
