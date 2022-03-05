package li.cil.scannable.common.container;

import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import li.cil.scannable.api.API;
import net.minecraft.core.Registry;
import net.minecraft.world.inventory.MenuType;

public final class Containers {
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(API.MOD_ID, Registry.MENU_REGISTRY);

    // --------------------------------------------------------------------- //

    public static final RegistrySupplier<MenuType<ScannerContainerMenu>> SCANNER_CONTAINER = CONTAINERS.register("scanner", () -> MenuRegistry.ofExtended(ScannerContainerMenu::create));
    public static final RegistrySupplier<MenuType<BlockModuleContainerMenu>> BLOCK_MODULE_CONTAINER = CONTAINERS.register("block_module", () -> MenuRegistry.ofExtended(BlockModuleContainerMenu::create));
    public static final RegistrySupplier<MenuType<EntityModuleContainerMenu>> ENTITY_MODULE_CONTAINER = CONTAINERS.register("entity_module", () -> MenuRegistry.ofExtended(EntityModuleContainerMenu::create));

    // --------------------------------------------------------------------- //

    public static void initialize() {
        CONTAINERS.register();
    }
}
