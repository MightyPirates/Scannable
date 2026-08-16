package li.cil.scannable.client.neoforge;

import li.cil.scannable.api.API;
import li.cil.scannable.client.ScanManager;
import li.cil.scannable.client.gui.ConfigurableBlockScannerModuleContainerScreen;
import li.cil.scannable.client.gui.ConfigurableEntityScannerModuleContainerScreen;
import li.cil.scannable.client.gui.ScannerContainerScreen;
import li.cil.scannable.client.renderer.OverlayRenderer;
import li.cil.scannable.client.renderer.ScannerRenderer;
import li.cil.scannable.common.container.Containers;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.joml.Matrix4f;

@EventBusSubscriber(modid = API.MOD_ID, value = Dist.CLIENT)
public final class ClientSetupNeoForge {
    @SubscribeEvent
    public static void handleSetupEvent(final FMLClientSetupEvent event) {
        NeoForge.EVENT_BUS.addListener(ClientSetupNeoForge::handleClientTickEvent);
        NeoForge.EVENT_BUS.addListener(ClientSetupNeoForge::handleRenderLevelEvent);
    }

    @SubscribeEvent
    public static void handleRegisterMenuScreensEvent(final RegisterMenuScreensEvent event) {
        event.register(Containers.SCANNER_CONTAINER.get(), ScannerContainerScreen::new);
        event.register(Containers.BLOCK_MODULE_CONTAINER.get(), ConfigurableBlockScannerModuleContainerScreen::new);
        event.register(Containers.ENTITY_MODULE_CONTAINER.get(), ConfigurableEntityScannerModuleContainerScreen::new);
    }

    @SubscribeEvent
    public static void handleRegisterLayersEvent(final RegisterGuiLayersEvent event) {
        event.registerAboveAll(Identifier.fromNamespaceAndPath(API.MOD_ID, "scanner_results"), (graphics, deltaTracker) -> {
            final float partialTick = deltaTracker.getGameTimeDeltaPartialTick(false);
            ScanManager.renderGui(partialTick);
            OverlayRenderer.render(graphics, partialTick);
        });
    }

    public static void handleClientTickEvent(final ClientTickEvent.Post event) {
        ScanManager.tick();
    }

    public static void handleRenderLevelEvent(final RenderLevelStageEvent.AfterLevel event) {
        final Minecraft mc = Minecraft.getInstance();

        final Matrix4f viewMatrix = new Matrix4f(event.getModelViewMatrix());
        final float partialTick = mc.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        final Matrix4f projectionMatrix = mc.gameRenderer.getProjectionMatrix(
            mc.gameRenderer.getFov(mc.gameRenderer.getMainCamera(), partialTick, true));

        ScannerRenderer.render(viewMatrix, projectionMatrix);

        ScanManager.setMatrices(viewMatrix, projectionMatrix);
        ScanManager.renderLevel(partialTick);
    }

    private ClientSetupNeoForge() {
    }
}
