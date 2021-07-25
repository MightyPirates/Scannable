package li.cil.scannable.common.container;

import li.cil.scannable.util.RegistryUtils;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class Containers {
    public static final DeferredRegister<MenuType<?>> CONTAINERS = RegistryUtils.create(ForgeRegistries.CONTAINERS);

    // --------------------------------------------------------------------- //

    public static final RegistryObject<MenuType<ContainerScanner>> SCANNER_CONTAINER = CONTAINERS.register("scanner", () -> IForgeContainerType.create(ContainerScanner::create));
    public static final RegistryObject<MenuType<BlockModuleContainer>> BLOCK_MODULE_CONTAINER = CONTAINERS.register("module_block", () -> IForgeContainerType.create(BlockModuleContainer::create));
    public static final RegistryObject<MenuType<EntityModuleContainer>> ENTITY_MODULE_CONTAINER = CONTAINERS.register("module_entity", () -> IForgeContainerType.create(EntityModuleContainer::create));

    // --------------------------------------------------------------------- //

    public static void initialize() {
    }
}
