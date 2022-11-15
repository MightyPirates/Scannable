package li.cil.scannable.client;

import dev.architectury.registry.menu.MenuRegistry;
import li.cil.scannable.client.gui.ConfigurableBlockScannerModuleContainerScreen;
import li.cil.scannable.client.gui.ConfigurableEntityScannerModuleContainerScreen;
import li.cil.scannable.client.gui.ScannerContainerScreen;
import li.cil.scannable.client.shader.Shaders;
import li.cil.scannable.common.container.Containers;

public class ClientSetup {
    public static void initialize() {
        MenuRegistry.registerScreenFactory(Containers.SCANNER_CONTAINER.get(), ScannerContainerScreen::new);
        MenuRegistry.registerScreenFactory(Containers.BLOCK_MODULE_CONTAINER.get(), ConfigurableBlockScannerModuleContainerScreen::new);
        MenuRegistry.registerScreenFactory(Containers.ENTITY_MODULE_CONTAINER.get(), ConfigurableEntityScannerModuleContainerScreen::new);

        Shaders.initialize();
    }
}
