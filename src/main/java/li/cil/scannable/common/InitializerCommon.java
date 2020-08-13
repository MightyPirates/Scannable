package li.cil.scannable.common;

import li.cil.scannable.common.capabilities.CapabilityScannerModule;
import li.cil.scannable.common.network.Network;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class InitializerCommon {
    public InitializerCommon() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::initializeCommon);
    }

    public void initializeCommon(final FMLCommonSetupEvent event) {
        Network.register();
        CapabilityScannerModule.register();
    }
}
