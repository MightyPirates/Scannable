package li.cil.scannable.common.scanning.forge;

import li.cil.scannable.api.API;
import li.cil.scannable.common.scanning.ChestScannerModule;
import li.cil.scannable.common.scanning.CommonOresBlockScannerModule;
import li.cil.scannable.common.scanning.FluidBlockScannerModule;
import li.cil.scannable.common.scanning.RareOresBlockScannerModule;
import li.cil.scannable.common.scanning.filter.IgnoredBlocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = API.MOD_ID, bus = Bus.MOD)
public final class ProviderCacheManagerImpl {
    public static void initialize() {
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onModConfigEvent(final ModConfigEvent configEvent) {
        // Reset on any config change, so we also rebuild the filter when resource reload
        // kicks in which can result in ids changing and thus our cache being invalid.
        CommonOresBlockScannerModule.clearCache();
        FluidBlockScannerModule.clearCache();
        RareOresBlockScannerModule.clearCache();
        ChestScannerModule.clearCache();
        IgnoredBlocks.clearCache();
    }
}
