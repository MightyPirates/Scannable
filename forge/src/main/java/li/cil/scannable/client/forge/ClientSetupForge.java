package li.cil.scannable.client.forge;

import li.cil.scannable.api.API;
import li.cil.scannable.client.ClientSetup;
import li.cil.scannable.client.ScanManager;
import li.cil.scannable.client.renderer.OverlayRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = API.MOD_ID, bus = Bus.MOD, value = Dist.CLIENT)
public final class ClientSetupForge {
    @SubscribeEvent
    public static void handleSetupEvent(final FMLClientSetupEvent event) {
        ClientSetup.initialize();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetupForge::handleRegisterOverlaysEvent);
    }

    @SubscribeEvent
    public static void handleRegisterOverlaysEvent(final RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("scanner_results", (gui, poseStack, partialTick, width, height) -> ScanManager.renderGui(partialTick));
        event.registerAboveAll("scanner_progress", (gui, poseStack, partialTick, width, height) -> OverlayRenderer.render(poseStack, partialTick));
    }
}
