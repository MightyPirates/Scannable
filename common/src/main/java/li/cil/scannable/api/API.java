package li.cil.scannable.api;

import net.minecraft.resources.ResourceLocation;

public final class API {
    public static final String MOD_ID = "scannable";

    // --------------------------------------------------------------------- //
    // Built-in icons that may be useful when rendering scan results.

    public static final ResourceLocation ICON_INFO = ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/gui/overlay/info.png");
    public static final ResourceLocation ICON_WARNING = ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/gui/overlay/warning.png");

    // --------------------------------------------------------------------- //
    // Registry names of reusable built-in scan providers.

    public static final ResourceLocation SCAN_RESULT_PROVIDER_BLOCKS = ResourceLocation.fromNamespaceAndPath(MOD_ID, "blocks");
    public static final ResourceLocation SCAN_RESULT_PROVIDER_ENTITIES = ResourceLocation.fromNamespaceAndPath(MOD_ID, "entities");

    // --------------------------------------------------------------------- //

    private API() {
    }
}
