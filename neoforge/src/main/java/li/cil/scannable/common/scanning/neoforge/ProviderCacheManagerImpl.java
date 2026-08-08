package li.cil.scannable.common.scanning.neoforge;

import li.cil.scannable.api.API;
import li.cil.scannable.common.scanning.ProviderCacheManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;

@EventBusSubscriber(modid = API.MOD_ID)
public final class ProviderCacheManagerImpl {
    public static void initialize() {
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onModConfigEvent(final ModConfigEvent configEvent) {
        ProviderCacheManager.clearCache();
    }
}
