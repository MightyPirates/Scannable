package li.cil.scannable.common;

import li.cil.scannable.api.API;
import li.cil.scannable.client.InitializerClient;
import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.config.Settings;
import li.cil.scannable.common.container.ContainerScanner;
import li.cil.scannable.common.item.*;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(API.MOD_ID)
public final class Scannable {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, API.MOD_ID);
    public static final RegistryObject<Item> MODULE_BLANK = ITEMS.register(Constants.NAME_MODULE_BLANK, ItemScannerModuleBlank::new);
    public static final RegistryObject<Item> MODULE_RANGE = ITEMS.register(Constants.NAME_MODULE_RANGE, ItemScannerModuleRange::new);
    public static final RegistryObject<Item> MODULE_ENTITY = ITEMS.register(Constants.NAME_MODULE_ENTITY, ItemScannerModuleEntityConfigurable::new);
    public static final RegistryObject<Item> MODULE_ANIMAL = ITEMS.register(Constants.NAME_MODULE_ANIMAL, ItemScannerModuleAnimal::new);
    public static final RegistryObject<Item> MODULE_MONSTER = ITEMS.register(Constants.NAME_MODULE_MONSTER, ItemScannerModuleMonster::new);
    public static final RegistryObject<Item> MODULE_BLOCK = ITEMS.register(Constants.NAME_MODULE_BLOCK, ItemScannerModuleBlockConfigurable::new);
    public static final RegistryObject<Item> MODULE_ORE_COMMON = ITEMS.register(Constants.NAME_MODULE_ORE_COMMON, ItemScannerModuleBlockOreCommon::new);
    public static final RegistryObject<Item> MODULE_ORE_RARE = ITEMS.register(Constants.NAME_MODULE_ORE_RARE, ItemScannerModuleBlockOreRare::new);
    public static final RegistryObject<Item> MODULE_FLUID = ITEMS.register(Constants.NAME_MODULE_FLUID, ItemScannerModuleBlockFluid::new);
    public static final RegistryObject<Item> MODULE_STRUCTURE = ITEMS.register(Constants.NAME_MODULE_STRUCTURE, ItemScannerModuleStructure::new);
    public static final RegistryObject<Item> SCANNER = ITEMS.register(Constants.NAME_SCANNER, ItemScanner::new);

    public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, API.MOD_ID);
    public static final RegistryObject<ContainerType<ContainerScanner>> SCANNER_CONTAINER = CONTAINERS.register(Constants.NAME_SCANNER, () -> IForgeContainerType.create(ContainerScanner::createForClient));

    public Scannable() {
        Settings.register();

        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());

        API.itemGroup = new ScannableItemGroup();

        DistExecutor.safeRunForDist(() -> InitializerClient::new, () -> InitializerCommon::new);
    }

    // --------------------------------------------------------------------- //

    private static final Logger LOGGER = LogManager.getLogger();

    public static Logger getLog() {
        return LOGGER;
    }
}
