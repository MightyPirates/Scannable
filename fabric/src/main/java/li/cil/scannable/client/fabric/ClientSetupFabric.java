/* SPDX-License-Identifier: MIT */

package li.cil.scannable.client.fabric;

import dev.architectury.registry.menu.MenuRegistry;
import li.cil.scannable.client.ClientSetup;
import li.cil.scannable.client.ScanManager;
import li.cil.scannable.client.gui.ConfigurableBlockScannerModuleContainerScreen;
import li.cil.scannable.client.gui.ConfigurableEntityScannerModuleContainerScreen;
import li.cil.scannable.client.gui.ScannerContainerScreen;
import li.cil.scannable.client.renderer.OverlayRenderer;
import li.cil.scannable.client.renderer.ScannerRenderer;
import li.cil.scannable.common.container.Containers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

public final class ClientSetupFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientSetup.initialize();

        MenuRegistry.registerScreenFactory(Containers.SCANNER_CONTAINER.get(), ScannerContainerScreen::new);
        MenuRegistry.registerScreenFactory(Containers.BLOCK_MODULE_CONTAINER.get(), ConfigurableBlockScannerModuleContainerScreen::new);
        MenuRegistry.registerScreenFactory(Containers.ENTITY_MODULE_CONTAINER.get(), ConfigurableEntityScannerModuleContainerScreen::new);

        ClientTickEvents.END_CLIENT_TICK.register(instance -> ScanManager.tick());
        WorldRenderEvents.LAST.register(context -> {
            // As of MC 1.21 the camera transform lives in the position (view) matrix;
            // context.matrixStack() is identity during level rendering.
            ScannerRenderer.render(context.positionMatrix(), context.projectionMatrix());

            ScanManager.setMatrices(context.positionMatrix(), context.projectionMatrix());
            ScanManager.renderLevel(context.tickCounter().getGameTimeDeltaPartialTick(false));
        });

        HudRenderCallback.EVENT.register((graphics, tickCounter) -> {
            final float partialTick = tickCounter.getGameTimeDeltaPartialTick(false);
            ScanManager.renderGui(partialTick);
            OverlayRenderer.render(graphics, partialTick);
        });
    }
}
