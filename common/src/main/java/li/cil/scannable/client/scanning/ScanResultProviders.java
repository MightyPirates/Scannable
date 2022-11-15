package li.cil.scannable.client.scanning;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import li.cil.scannable.api.API;
import li.cil.scannable.api.scanning.ScanResultProvider;
import li.cil.scannable.util.RegistryUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class ScanResultProviders {
    private static final DeferredRegister<ScanResultProvider> DEFERRED_REGISTER = RegistryUtils.get(ScanResultProvider.REGISTRY);

    // --------------------------------------------------------------------- //

    public static final RegistrySupplier<ScanResultProviderBlock> BLOCKS = DEFERRED_REGISTER.register(
        API.SCAN_RESULT_PROVIDER_BLOCKS.getPath(), ScanResultProviderBlock::new);
    public static final RegistrySupplier<ScanResultProviderEntity> ENTITIES = DEFERRED_REGISTER.register(
        API.SCAN_RESULT_PROVIDER_ENTITIES.getPath(), ScanResultProviderEntity::new);

    // --------------------------------------------------------------------- //

    public static void initialize() {
        RegistryUtils.builder(ScanResultProvider.REGISTRY).build();
    }
}
