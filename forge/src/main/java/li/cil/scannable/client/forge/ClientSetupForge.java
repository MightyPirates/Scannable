package li.cil.scannable.client.forge;

import li.cil.scannable.api.API;
import li.cil.scannable.client.ClientSetup;
import li.cil.scannable.client.ScanManager;
import li.cil.scannable.client.renderer.OverlayRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = API.MOD_ID, bus = Bus.MOD, value = Dist.CLIENT)
public final class ClientSetupForge {
    @SubscribeEvent
    public static void handleSetupEvent(final FMLClientSetupEvent event) {
        ClientSetup.initialize();

        MinecraftForge.EVENT_BUS.addListener(ClientSetupForge::handleClientTickEvent);
        MinecraftForge.EVENT_BUS.addListener(ClientSetupForge::handleRenderLevelEvent);
    }

    @SubscribeEvent
    public static void handleRegisterOverlaysEvent(final RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("scanner_results", (gui, poseStack, partialTick, width, height) -> {
            ScanManager.renderGui(partialTick);
            OverlayRenderer.render(poseStack, partialTick);
        });
    }

    public static void handleClientTickEvent(final TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ScanManager.tick();
        }
    }

    public static void handleRenderLevelEvent(final RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_WEATHER) {
            ScanManager.renderLevel(event.getPoseStack(), event.getProjectionMatrix(), event.getPartialTick());
        }
    }
}
