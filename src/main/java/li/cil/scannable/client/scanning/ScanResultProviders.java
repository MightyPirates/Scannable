package li.cil.scannable.client.scanning;

import li.cil.scannable.api.API;
import li.cil.scannable.api.scanning.ScanResultProvider;
import li.cil.scannable.util.RegistryUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryBuilder;

@OnlyIn(Dist.CLIENT)
public final class ScanResultProviders {
    private static final DeferredRegister<ScanResultProvider> DEFERRED_REGISTER = RegistryUtils.create(ScanResultProvider.class);

    // --------------------------------------------------------------------- //

    public static final RegistryObject<ScanResultProviderBlock> BLOCKS = DEFERRED_REGISTER.register(
            API.SCAN_RESULT_PROVIDER_BLOCKS.getPath(), ScanResultProviderBlock::new);
    public static final RegistryObject<ScanResultProviderEntity> ENTITIES = DEFERRED_REGISTER.register(
            API.SCAN_RESULT_PROVIDER_ENTITIES.getPath(), ScanResultProviderEntity::new);

    // --------------------------------------------------------------------- //

    public static void initialize() {
        DEFERRED_REGISTER.makeRegistry(API.SCAN_RESULT_PROVIDER_REGISTRY.getPath(), () ->
                new RegistryBuilder<ScanResultProvider>().disableSync().disableSaving());
    }
}
