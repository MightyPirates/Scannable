package li.cil.scannable.client;

import li.cil.scannable.api.API;
import li.cil.scannable.client.gui.GuiHandlerClient;
import li.cil.scannable.client.renderer.OverlayRenderer;
import li.cil.scannable.client.renderer.ScannerRenderer;
import li.cil.scannable.client.scanning.ScanResultProviderOre;
import li.cil.scannable.common.ProxyCommon;
import li.cil.scannable.common.Scannable;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.apache.commons.lang3.ObjectUtils;

import java.util.function.Supplier;

/**
 * Takes care of client-side only setup.
 */
public final class ProxyClient extends ProxyCommon {
    @Override
    public void onInit(final FMLInitializationEvent event) {
        super.onInit(event);

        NetworkRegistry.INSTANCE.registerGuiHandler(Scannable.instance, new GuiHandlerClient());

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(ScannerRenderer.INSTANCE);
        MinecraftForge.EVENT_BUS.register(OverlayRenderer.INSTANCE);
        MinecraftForge.EVENT_BUS.register(ScanManager.INSTANCE);
    }

    @Override
    public void onPostInit(final FMLPostInitializationEvent event) {
        super.onPostInit(event);

        ScannerRenderer.INSTANCE.init();
    }

    // --------------------------------------------------------------------- //

    @Override
    public Item registerItem(final String name, final Supplier<Item> constructor) {
        final Item item = super.registerItem(name, constructor);
        setCustomItemModelResourceLocation(item);
        return item;
    }

    // --------------------------------------------------------------------- //

    @SubscribeEvent
    public void handleConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
        if (ObjectUtils.notEqual(API.MOD_ID, event.getModID())) {
            return;
        }

        ConfigManager.sync(API.MOD_ID, Config.Type.INSTANCE);
        ScanResultProviderOre.INSTANCE.rebuildOreCache();
    }

    // --------------------------------------------------------------------- //

    private static void setCustomItemModelResourceLocation(final Item item) {
        final ResourceLocation registryName = item.getRegistryName();
        assert registryName != null;
        final ModelResourceLocation location = new ModelResourceLocation(registryName, "inventory");
        ModelLoader.setCustomModelResourceLocation(item, 0, location);
    }
}
