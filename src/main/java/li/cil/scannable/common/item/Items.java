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

    public static final RegistryObject<Item> BLANK_MODULE = ITEMS.register("blank_module", ModItem::new);
    public static final RegistryObject<Item> RANGE_MODULE = ITEMS.register("range_module", () -> new ScannerModuleItem(RangeScannerModule.INSTANCE));
    public static final RegistryObject<Item> ENTITY_MODULE = ITEMS.register("entity_module", ConfigurableEntityScannerModuleItem::new);
    public static final RegistryObject<Item> FRIENDLY_ENTITY_MODULE = ITEMS.register("friendly_entity_module", () -> new ScannerModuleItem(FriendlyEntityScannerModule.INSTANCE));
    public static final RegistryObject<Item> HOSTILE_ENTITY_MODULE = ITEMS.register("hostile_entity_module", () -> new ScannerModuleItem(HostileEntityScannerModule.INSTANCE));
    public static final RegistryObject<Item> BLOCK_MODULE = ITEMS.register("block_module", ConfigurableBlockScannerModuleItem::new);
    public static final RegistryObject<Item> COMMON_ORES_MODULE = ITEMS.register("common_ores_module", () -> new ScannerModuleItem(CommonOresBlockScannerModule.INSTANCE));
    public static final RegistryObject<Item> RARE_ORES_MODULE = ITEMS.register("rare_ores_module", () -> new ScannerModuleItem(RareOresBlockScannerModule.INSTANCE));
    public static final RegistryObject<Item> FLUID_MODULE = ITEMS.register("fluid_module", () -> new ScannerModuleItem(FluidBlockScannerModule.INSTANCE));
//    public static final RegistryObject<Item> STRUCTURES_MODULE = ITEMS.register("structures_module", StructureScannerModuleItem::new);

    // --------------------------------------------------------------------- //

    public static void initialize() {
    }
}
