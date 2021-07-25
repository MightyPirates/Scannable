package li.cil.scannable.common.item;

import li.cil.scannable.common.scanning.*;
import li.cil.scannable.util.RegistryUtils;
import net.minecraft.world.item.Item;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class Items {
    private static final DeferredRegister<Item> ITEMS = RegistryUtils.create(ForgeRegistries.ITEMS);

    // --------------------------------------------------------------------- //

    public static final RegistryObject<Item> SCANNER = ITEMS.register("scanner", ScannerItem::new);

    public static final RegistryObject<Item> MODULE_BLANK = ITEMS.register("module_blank", ModItem::new);
    public static final RegistryObject<Item> MODULE_RANGE = ITEMS.register("module_range", () -> new ScannerModuleItem(RangeScannerModule.INSTANCE));
    public static final RegistryObject<Item> MODULE_ENTITY = ITEMS.register("module_entity", ConfigurableEntityScannerModuleItem::new);
    public static final RegistryObject<Item> MODULE_ANIMAL = ITEMS.register("module_animal", () -> new ScannerModuleItem(FriendlyEntityScannerModule.INSTANCE));
    public static final RegistryObject<Item> MODULE_MONSTER = ITEMS.register("module_monster", () -> new ScannerModuleItem(HostileEntityScannerModule.INSTANCE));
    public static final RegistryObject<Item> MODULE_BLOCK = ITEMS.register("module_block", ConfigurableBlockScannerModuleItem::new);
    public static final RegistryObject<Item> MODULE_ORE_COMMON = ITEMS.register("module_ore_common", () -> new ScannerModuleItem(CommonOreBlockScannerModule.INSTANCE));
    public static final RegistryObject<Item> MODULE_ORE_RARE = ITEMS.register("module_ore_rare", () -> new ScannerModuleItem(RareOreBlockScannerModule.INSTANCE));
    public static final RegistryObject<Item> MODULE_FLUID = ITEMS.register("module_fluid", () -> new ScannerModuleItem(FluidBlockScannerModule.INSTANCE));
//     public static final RegistryObject<Item> MODULE_STRUCTURE = ITEMS.register("module_structure", ItemScannerModuleStructure::new);

    // --------------------------------------------------------------------- //

    public static void initialize() {
    }
}
