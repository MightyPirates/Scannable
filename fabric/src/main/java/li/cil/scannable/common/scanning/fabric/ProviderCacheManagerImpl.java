package li.cil.scannable.common.scanning.fabric;

import fuzs.forgeconfigapiport.api.config.v2.ModConfigEvents;
import li.cil.scannable.api.API;
import li.cil.scannable.common.scanning.ProviderCacheManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

public final class ProviderCacheManagerImpl {
    public static void initialize() {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) {
            return;
        }

        ModConfigEvents.loading(API.MOD_ID).register((cfg) -> clearCaches());
        ModConfigEvents.reloading(API.MOD_ID).register((cfg) -> clearCaches());
    }

    private static void clearCaches() {
        ProviderCacheManager.clearCache();
    }
}
