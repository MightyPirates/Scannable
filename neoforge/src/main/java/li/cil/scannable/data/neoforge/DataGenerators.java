package li.cil.scannable.data.neoforge;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber
public final class DataGenerators {
    @SubscribeEvent
    public static void gatherClientData(final GatherDataEvent.Client event) {
        event.createProvider(ModItemModelProvider::new);
    }

    @SubscribeEvent
    public static void gatherServerData(final GatherDataEvent.Server event) {
        event.createProvider(ModItemTagsProvider::new);
        event.createProvider(ModRecipeProvider::new);
    }

    private DataGenerators() {
    }
}
