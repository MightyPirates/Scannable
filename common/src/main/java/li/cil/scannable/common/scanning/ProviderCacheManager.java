package li.cil.scannable.common.scanning;

import dev.architectury.injectables.annotations.ExpectPlatform;
import li.cil.scannable.common.scanning.filter.IgnoredBlocks;

public final class ProviderCacheManager {
    @ExpectPlatform
    public static void initialize() {
        throw new AssertionError();
    }

    public static void clearCache() {
        // Reset on any config change, so we also rebuild the filter when resource reload
        // kicks in which can result in ids changing and thus our cache being invalid.
        CommonOresBlockScannerModule.clearCache();
        FluidBlockScannerModule.clearCache();
        RareOresBlockScannerModule.clearCache();
        ChestScannerModule.clearCache();
        IgnoredBlocks.clearCache();
    }
}
