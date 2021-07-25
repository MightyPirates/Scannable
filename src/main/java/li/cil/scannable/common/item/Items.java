package li.cil.scannable.common.item;

import li.cil.scannable.util.RegistryUtils;
import net.minecraft.world.item.Item;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class Items {
    private static final DeferredRegister<Item> ITEMS = RegistryUtils.create(ForgeRegistries.ITEMS);

    // --------------------------------------------------------------------- //

    public static final RegistryObject<Item> SCANNER = ITEMS.register("scanner", ItemScanner::new);

    public static final RegistryObject<Item> MODULE_BLANK = ITEMS.register("module_blank", ItemScannerModuleBlank::new);
    public static final RegistryObject<Item> MODULE_RANGE = ITEMS.register("module_range", ItemScannerModuleRange::new);
    public static final RegistryObject<Item> MODULE_ENTITY = ITEMS.register("module_entity", ItemScannerModuleEntityConfigurable::new);
    public static final RegistryObject<Item> MODULE_ANIMAL = ITEMS.register("module_animal", ItemScannerModuleAnimal::new);
    public static final RegistryObject<Item> MODULE_MONSTER = ITEMS.register("module_monster", ItemScannerModuleMonster::new);
    public static final RegistryObject<Item> MODULE_BLOCK = ITEMS.register("module_block", ItemScannerModuleBlockConfigurable::new);
    public static final RegistryObject<Item> MODULE_ORE_COMMON = ITEMS.register("module_ore_common", ItemScannerModuleBlockOreCommon::new);
    public static final RegistryObject<Item> MODULE_ORE_RARE = ITEMS.register("module_ore_rare", ItemScannerModuleBlockOreRare::new);
    public static final RegistryObject<Item> MODULE_FLUID = ITEMS.register("module_fluid", ItemScannerModuleBlockFluid::new);
//     public static final RegistryObject<Item> MODULE_STRUCTURE = ITEMS.register("module_structure", ItemScannerModuleStructure::new);

    // --------------------------------------------------------------------- //

    public static void initialize() {
    }
}
