package li.cil.scannable.common.container;

import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import li.cil.scannable.util.RegistryUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;

public final class Containers {
    public static final DeferredRegister<MenuType<?>> CONTAINERS = RegistryUtils.get(Registries.MENU);

    // --------------------------------------------------------------------- //

    public static final RegistrySupplier<MenuType<ScannerContainerMenu>> SCANNER_CONTAINER = CONTAINERS.register("scanner", () -> MenuRegistry.ofExtended(ScannerContainerMenu::create));
    public static final RegistrySupplier<MenuType<BlockModuleContainerMenu>> BLOCK_MODULE_CONTAINER = CONTAINERS.register("block_module", () -> MenuRegistry.ofExtended(BlockModuleContainerMenu::create));
    public static final RegistrySupplier<MenuType<EntityModuleContainerMenu>> ENTITY_MODULE_CONTAINER = CONTAINERS.register("entity_module", () -> MenuRegistry.ofExtended(EntityModuleContainerMenu::create));

    // --------------------------------------------------------------------- //

    public static void initialize() {
    }
}
