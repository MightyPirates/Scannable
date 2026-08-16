package li.cil.scannable.api;

import net.minecraft.resources.Identifier;

public final class API {
    public static final String MOD_ID = "scannable";

    // --------------------------------------------------------------------- //
    // Built-in icons that may be useful when rendering scan results.

    public static final Identifier ICON_INFO = Identifier.fromNamespaceAndPath(MOD_ID, "textures/gui/overlay/info.png");
    public static final Identifier ICON_WARNING = Identifier.fromNamespaceAndPath(MOD_ID, "textures/gui/overlay/warning.png");

    // --------------------------------------------------------------------- //
    // Registry names of reusable built-in scan providers.

    public static final Identifier SCAN_RESULT_PROVIDER_BLOCKS = Identifier.fromNamespaceAndPath(MOD_ID, "blocks");
    public static final Identifier SCAN_RESULT_PROVIDER_ENTITIES = Identifier.fromNamespaceAndPath(MOD_ID, "entities");

    // --------------------------------------------------------------------- //

    private API() {
    }
}
