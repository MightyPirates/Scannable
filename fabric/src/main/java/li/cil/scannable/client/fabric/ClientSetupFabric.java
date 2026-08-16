package li.cil.scannable.client.fabric;

import com.mojang.blaze3d.systems.RenderSystem;
import li.cil.scannable.api.API;
import li.cil.scannable.client.ScanManager;
import li.cil.scannable.client.gui.ConfigurableBlockScannerModuleContainerScreen;
import li.cil.scannable.client.gui.ConfigurableEntityScannerModuleContainerScreen;
import li.cil.scannable.client.gui.ScannerContainerScreen;
import li.cil.scannable.client.renderer.OverlayRenderer;
import li.cil.scannable.client.renderer.ScannerRenderer;
import li.cil.scannable.common.container.Containers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;

public final class ClientSetupFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MenuScreens.register(Containers.SCANNER_CONTAINER.get(), ScannerContainerScreen::new);
        MenuScreens.register(Containers.BLOCK_MODULE_CONTAINER.get(), ConfigurableBlockScannerModuleContainerScreen::new);
        MenuScreens.register(Containers.ENTITY_MODULE_CONTAINER.get(), ConfigurableEntityScannerModuleContainerScreen::new);

        ClientTickEvents.END_CLIENT_TICK.register(instance -> ScanManager.tick());

        WorldRenderEvents.END_MAIN.register(context -> {
            final Minecraft mc = Minecraft.getInstance();
            final Matrix4f viewMatrix = new Matrix4f(RenderSystem.getModelViewMatrix());
            final float partialTick = mc.getDeltaTracker().getGameTimeDeltaPartialTick(false);
            final Matrix4f projectionMatrix = mc.gameRenderer.getProjectionMatrix(
                mc.gameRenderer.getFov(mc.gameRenderer.getMainCamera(), partialTick, true));

            ScannerRenderer.render(viewMatrix, projectionMatrix);

            ScanManager.setMatrices(viewMatrix, projectionMatrix);
            ScanManager.renderLevel(partialTick);
        });

        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath(API.MOD_ID, "scanner_results"),
            (graphics, tickCounter) -> {
                final float partialTick = tickCounter.getGameTimeDeltaPartialTick(false);
                ScanManager.renderGui(partialTick);
                OverlayRenderer.render(graphics, partialTick);
            });
    }
}
