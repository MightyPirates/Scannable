package li.cil.scannable.common;


import li.cil.scannable.api.API;
import li.cil.scannable.common.config.Constants;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

/**
 * Entry point for FML.
 */
@Mod(modid = API.MOD_ID, version = API.MOD_VERSION, name = Constants.MOD_NAME,
     useMetadata = true)
public final class Scannable {
    // --------------------------------------------------------------------- //
    // FML / Forge

    @Mod.Instance(API.MOD_ID)
    public static Scannable instance;

    @SidedProxy(clientSide = Constants.PROXY_CLIENT, serverSide = Constants.PROXY_COMMON)
    public static ProxyCommon proxy;

    @Mod.EventHandler
    public void onPreInit(final FMLPreInitializationEvent event) {
        log = event.getModLog();
        proxy.onPreInit(event);
    }

    @Mod.EventHandler
    public void onInit(final FMLInitializationEvent event) {
        proxy.onInit(event);
    }

    @Mod.EventHandler
    public void onPostInit(final FMLPostInitializationEvent event) {
        proxy.onPostInit(event);
    }

    // --------------------------------------------------------------------- //

    /**
     * Logger the mod should use, filled in pre-init.
     */
    private static Logger log;

    /**
     * Get the logger to be used by the mod.
     *
     * @return the mod's logger.
     */
    public static Logger getLog() {
        return log;
    }
}
