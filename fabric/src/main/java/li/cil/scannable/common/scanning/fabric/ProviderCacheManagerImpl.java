package li.cil.scannable.common.scanning.fabric;

import fuzs.forgeconfigapiport.api.config.v2.ModConfigEvents;
import li.cil.scannable.api.API;
import li.cil.scannable.common.scanning.CommonOresBlockScannerModule;
import li.cil.scannable.common.scanning.FluidBlockScannerModule;
import li.cil.scannable.common.scanning.RareOresBlockScannerModule;
import li.cil.scannable.common.scanning.filter.IgnoredBlocks;
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
        // Reset on any config change, so we also rebuild the filter when resource reload
        // kicks in which can result in ids changing and thus our cache being invalid.
        CommonOresBlockScannerModule.clearCache();
        FluidBlockScannerModule.clearCache();
        RareOresBlockScannerModule.clearCache();
        IgnoredBlocks.clearCache();
    }
}
