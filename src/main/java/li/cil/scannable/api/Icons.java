package li.cil.scannable.api;

import net.minecraft.util.ResourceLocation;

/**
 * List of built-in icons that may be useful when rendering scan results.
 */
public final class Icons {
    public static final ResourceLocation INFO = new ResourceLocation(API.MOD_ID, "textures/overlay/info.png");
    public static final ResourceLocation WARNING = new ResourceLocation(API.MOD_ID, "textures/overlay/warning.png");

    // --------------------------------------------------------------------- //

    private Icons() {
    }
}
