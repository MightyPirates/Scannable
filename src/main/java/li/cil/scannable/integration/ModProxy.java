package li.cil.scannable.integration;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public interface ModProxy {
    boolean isAvailable();

    default void preInit(final FMLPreInitializationEvent event) {
    }

    default void init(final FMLInitializationEvent event) {
    }

    default void postInit(final FMLPostInitializationEvent event) {
    }
}
