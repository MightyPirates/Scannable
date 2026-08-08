package li.cil.scannable.common.scanning.fabric;

import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeModConfigEvents;
import li.cil.scannable.api.API;
import li.cil.scannable.common.scanning.ProviderCacheManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

public final class ProviderCacheManagerImpl {
    public static void initialize() {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) {
            return;
        }

        NeoForgeModConfigEvents.loading(API.MOD_ID).register((cfg) -> clearCaches());
        NeoForgeModConfigEvents.reloading(API.MOD_ID).register((cfg) -> clearCaches());
    }

    private static void clearCaches() {
        ProviderCacheManager.clearCache();
    }
}
