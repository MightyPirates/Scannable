package li.cil.scannable.common;

import li.cil.scannable.api.API;
import li.cil.scannable.common.capabilities.CapabilityScanResultProvider;
import li.cil.scannable.common.init.Items;
import li.cil.scannable.common.network.Network;
import li.cil.scannable.integration.Integration;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Takes care of common setup.
 */
@Mod.EventBusSubscriber
public class ProxyCommon {
    public void onPreInit(final FMLPreInitializationEvent event) {
        // Initialize API.
        API.creativeTab = new CreativeTab();

        // Initialize capabilities.
        CapabilityScanResultProvider.register();

        // Mod integration.
        Integration.preInit(event);
    }

    public void onInit(final FMLInitializationEvent event) {
        // Register network handler.
        Network.INSTANCE.init();

        // Mod integration.
        Integration.init(event);
    }

    public void onPostInit(final FMLPostInitializationEvent event) {
        // Mod integration.
        Integration.postInit(event);
    }

    // --------------------------------------------------------------------- //

    @SubscribeEvent
    public static void handleRegisterItemsEvent(final RegistryEvent.Register<Item> event) {
        Items.register(event.getRegistry());
    }
}
