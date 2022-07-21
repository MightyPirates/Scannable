package li.cil.scannable.common.container;

import li.cil.scannable.util.RegistryUtils;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class Containers {
    public static final DeferredRegister<MenuType<?>> CONTAINERS = RegistryUtils.getInitializerFor(ForgeRegistries.MENU_TYPES);

    // --------------------------------------------------------------------- //

    public static final RegistryObject<MenuType<ScannerContainerMenu>> SCANNER_CONTAINER = CONTAINERS.register("scanner", () -> IForgeMenuType.create(ScannerContainerMenu::create));
    public static final RegistryObject<MenuType<BlockModuleContainerMenu>> BLOCK_MODULE_CONTAINER = CONTAINERS.register("block_module", () -> IForgeMenuType.create(BlockModuleContainerMenu::create));
    public static final RegistryObject<MenuType<EntityModuleContainerMenu>> ENTITY_MODULE_CONTAINER = CONTAINERS.register("entity_module", () -> IForgeMenuType.create(EntityModuleContainerMenu::create));

    // --------------------------------------------------------------------- //

    public static void initialize() {
    }
}
