package li.cil.scannable.client;

import dev.architectury.registry.menu.MenuRegistry;
import li.cil.scannable.client.gui.ConfigurableBlockScannerModuleContainerScreen;
import li.cil.scannable.client.gui.ConfigurableEntityScannerModuleContainerScreen;
import li.cil.scannable.client.gui.ScannerContainerScreen;
import li.cil.scannable.client.scanning.ScanResultProviders;
import li.cil.scannable.client.shader.Shaders;
import li.cil.scannable.common.container.Containers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

public final class ScannableClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ScanResultProviders.initialize();

        MenuRegistry.registerScreenFactory(Containers.SCANNER_CONTAINER.get(), ScannerContainerScreen::new);
        MenuRegistry.registerScreenFactory(Containers.BLOCK_MODULE_CONTAINER.get(), ConfigurableBlockScannerModuleContainerScreen::new);
        MenuRegistry.registerScreenFactory(Containers.ENTITY_MODULE_CONTAINER.get(), ConfigurableEntityScannerModuleContainerScreen::new);

        ClientTickEvents.END_CLIENT_TICK.register(ScanManager::onClientTick);
        WorldRenderEvents.LAST.register(ScanManager::onRenderLast);

        Shaders.initialize();
    }
}
