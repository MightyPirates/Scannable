package li.cil.scannable.client.scanning;

import li.cil.scannable.api.API;
import li.cil.scannable.api.scanning.ScanResultProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

@OnlyIn(Dist.CLIENT)
public final class ScanResultProviderRegistryInitializer {
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerScanResultProviderRegistry(final RegistryEvent.NewRegistry event) {
        new RegistryBuilder<ScanResultProvider>()
                .setName(API.SCAN_RESULT_PROVIDER_REGISTRY)
                .setType(ScanResultProvider.class)
                .disableSync()
                .disableSaving()
                .create();
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerScanResultProviders(final RegistryEvent.Register<?> event) {
        if (event.getRegistry().getRegistrySuperType() != ScanResultProvider.class) {
            return;
        }

        @SuppressWarnings("unchecked") final IForgeRegistry<ScanResultProvider> registry = (IForgeRegistry<ScanResultProvider>) event.getRegistry();
        ScanResultProviderBlock.INSTANCE.setRegistryName(API.SCAN_RESULT_PROVIDER_BLOCKS);
        registry.register(ScanResultProviderBlock.INSTANCE);

        ScanResultProviderEntity.INSTANCE.setRegistryName(API.SCAN_RESULT_PROVIDER_ENTITIES);
        registry.register(ScanResultProviderEntity.INSTANCE);
    }
}
