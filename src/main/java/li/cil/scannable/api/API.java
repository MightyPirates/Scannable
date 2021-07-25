package li.cil.scannable.api;

import net.minecraft.resources.ResourceLocation;

public final class API {
    public static final String MOD_ID = "scannable";

    // --------------------------------------------------------------------- //
    // Built-in icons that may be useful when rendering scan results.

    public static final ResourceLocation ICON_INFO = new ResourceLocation(MOD_ID, "textures/gui/overlay/info.png");
    public static final ResourceLocation ICON_WARNING = new ResourceLocation(MOD_ID, "textures/gui/overlay/warning.png");

    // --------------------------------------------------------------------- //
    // Registry names of reusable built-in scan providers.

    public static final ResourceLocation SCAN_RESULT_PROVIDER_REGISTRY = new ResourceLocation(MOD_ID, "scan_result_providers");
    public static final ResourceLocation SCAN_RESULT_PROVIDER_BLOCKS = new ResourceLocation(MOD_ID, "blocks");
    public static final ResourceLocation SCAN_RESULT_PROVIDER_ENTITIES = new ResourceLocation(MOD_ID, "entities");

    // --------------------------------------------------------------------- //

    private API() {
    }
}
