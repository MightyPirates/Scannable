package li.cil.scannable.integration;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.ArrayList;
import java.util.List;

public enum Integration {
    INSTANCE;

    private static final List<ModProxy> proxies = new ArrayList<>();

    static {
    }

    // --------------------------------------------------------------------- //

    public static void preInit(final FMLPreInitializationEvent event) {
        proxies.stream().filter(ModProxy::isAvailable).forEach(proxy -> proxy.preInit(event));
    }

    public static void init(final FMLInitializationEvent event) {
        proxies.stream().filter(ModProxy::isAvailable).forEach(proxy -> proxy.init(event));
    }

    public static void postInit(final FMLPostInitializationEvent event) {
        proxies.stream().filter(ModProxy::isAvailable).forEach(proxy -> proxy.postInit(event));
    }
}
