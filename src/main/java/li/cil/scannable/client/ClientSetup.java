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
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
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

        MinecraftForge.EVENT_BUS.addListener(ScanManager::onClientTick);
        MinecraftForge.EVENT_BUS.addListener(ScanManager::renderLevel);

        Shaders.initialize();
    }

    @SubscribeEvent
    public static void handleRegisterOverlaysEvent(final RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("scanner_results", (gui, poseStack, partialTick, width, height) -> ScanManager.renderGui(partialTick));
        event.registerAboveAll("scanner_progress", (gui, poseStack, partialTick, width, height) -> OverlayRenderer.render(poseStack, partialTick));
    }
}
