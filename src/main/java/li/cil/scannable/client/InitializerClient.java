package li.cil.scannable.client;

import li.cil.scannable.api.scanning.ScanResultProvider;
import li.cil.scannable.client.gui.BlockModuleScreen;
import li.cil.scannable.client.gui.EntityModuleScreen;
import li.cil.scannable.client.gui.GuiScanner;
import li.cil.scannable.client.renderer.OverlayRenderer;
import li.cil.scannable.client.renderer.ScannerRenderer;
import li.cil.scannable.client.scanning.ScanResultProviderRegistryInitializer;
import li.cil.scannable.client.shader.ScanEffectShader;
import li.cil.scannable.client.shader.ScanResultShader;
import li.cil.scannable.common.InitializerCommon;
import li.cil.scannable.common.Scannable;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public final class InitializerClient extends InitializerCommon {
    public InitializerClient() {
        super();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::initializeClient);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(ScanResultProviderRegistryInitializer::registerScanResultProviderRegistry);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(ScanResultProvider.class, ScanResultProviderRegistryInitializer::registerScanResultProviders);
    }

    public void initializeClient(final FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(ScannerRenderer.INSTANCE);
        MinecraftForge.EVENT_BUS.register(OverlayRenderer.INSTANCE);
        MinecraftForge.EVENT_BUS.register(ScanManager.INSTANCE);

        ScanEffectShader.INSTANCE.initialize();
        ScanResultShader.INSTANCE.initialize();

        ScreenManager.registerFactory(Scannable.SCANNER_CONTAINER.get(), GuiScanner::new);
        ScreenManager.registerFactory(Scannable.BLOCK_MODULE_CONTAINER.get(), BlockModuleScreen::new);
        ScreenManager.registerFactory(Scannable.ENTITY_MODULE_CONTAINER.get(), EntityModuleScreen::new);
    }
}
