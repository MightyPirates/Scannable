package li.cil.scannable.common;

import li.cil.scannable.common.network.Network;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public final class CommonSetup {
    @SubscribeEvent
    public static void handleSetupEvent(final FMLCommonSetupEvent event) {
        Network.initialize();
    }
}
