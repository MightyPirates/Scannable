package li.cil.scannable.client.scanning;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import li.cil.scannable.api.API;
import li.cil.scannable.api.scanning.ScanResultProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.Registry;

@Environment(EnvType.CLIENT)
public final class ScanResultProviders {
    public static final Registry<ScanResultProvider> PROVIDER_REGISTRY = FabricRegistryBuilder
        .createSimple(ScanResultProvider.class, ScanResultProvider.REGISTRY.location())
        .buildAndRegister();
    private static final DeferredRegister<ScanResultProvider> DEFERRED_REGISTER = DeferredRegister.create(API.MOD_ID, ScanResultProvider.REGISTRY);

    // --------------------------------------------------------------------- //

    public static final RegistrySupplier<ScanResultProviderBlock> BLOCKS = DEFERRED_REGISTER.register(
            API.SCAN_RESULT_PROVIDER_BLOCKS.getPath(), ScanResultProviderBlock::new);
    public static final RegistrySupplier<ScanResultProviderEntity> ENTITIES = DEFERRED_REGISTER.register(
            API.SCAN_RESULT_PROVIDER_ENTITIES.getPath(), ScanResultProviderEntity::new);

    // --------------------------------------------------------------------- //

    public static void initialize() {
        DEFERRED_REGISTER.register();
    }
}
