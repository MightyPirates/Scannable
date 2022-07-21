package li.cil.scannable.client;

import li.cil.scannable.client.gui.ConfigurableBlockScannerModuleContainerScreen;
import li.cil.scannable.client.gui.ConfigurableEntityScannerModuleContainerScreen;
import li.cil.scannable.client.gui.ScannerContainerScreen;
import li.cil.scannable.client.renderer.OverlayRenderer;
import li.cil.scannable.client.shader.Shaders;
import li.cil.scannable.common.container.Containers;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@OnlyIn(Dist.CLIENT)
public final class ClientSetup {
    @SubscribeEvent
    public static void handleSetupEvent(final FMLClientSetupEvent event) {
        MenuScreens.register(Containers.SCANNER_CONTAINER.get(), ScannerContainerScreen::new);
        MenuScreens.register(Containers.BLOCK_MODULE_CONTAINER.get(), ConfigurableBlockScannerModuleContainerScreen::new);
        MenuScreens.register(Containers.ENTITY_MODULE_CONTAINER.get(), ConfigurableEntityScannerModuleContainerScreen::new);

        MinecraftForge.EVENT_BUS.addListener(OverlayRenderer::onOverlayRender);
        MinecraftForge.EVENT_BUS.addListener(ScanManager::onClientTick);
        MinecraftForge.EVENT_BUS.addListener(ScanManager::onPostRenderLevel);
        MinecraftForge.EVENT_BUS.addListener(ScanManager::onPreRenderGameOverlay);

        Shaders.initialize();
    }
}
