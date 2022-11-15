package li.cil.scannable.client.forge;

import li.cil.scannable.api.API;
import li.cil.scannable.client.ScanManager;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = API.MOD_ID, bus = Bus.FORGE)
public final class ScanManagerEvents {
    @SubscribeEvent
    public static void handleClientTickEvent(final TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        ScanManager.tick();
    }

    @SubscribeEvent
    public static void handleRenderLevelEvent(final RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS) {
            return;
        }

        ScanManager.renderLevel(event.getPoseStack(), event.getProjectionMatrix(), event.getPartialTick());
    }
}
