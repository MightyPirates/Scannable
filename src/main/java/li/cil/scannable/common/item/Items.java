package li.cil.scannable.common.item;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import li.cil.scannable.api.API;
import li.cil.scannable.common.scanning.*;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;

public final class Items {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(API.MOD_ID, Registry.ITEM_REGISTRY);

    // --------------------------------------------------------------------- //

    public static final RegistrySupplier<Item> SCANNER = ITEMS.register("scanner", ScannerItem::new);

    public static final RegistrySupplier<Item> BLANK_MODULE = ITEMS.register("blank_module", ModItem::new);
    public static final RegistrySupplier<Item> RANGE_MODULE = ITEMS.register("range_module", () -> new ScannerModuleItem(RangeScannerModule.INSTANCE));
    public static final RegistrySupplier<Item> ENTITY_MODULE = ITEMS.register("entity_module", ConfigurableEntityScannerModuleItem::new);
    public static final RegistrySupplier<Item> FRIENDLY_ENTITY_MODULE = ITEMS.register("friendly_entity_module", () -> new ScannerModuleItem(FriendlyEntityScannerModule.INSTANCE));
    public static final RegistrySupplier<Item> HOSTILE_ENTITY_MODULE = ITEMS.register("hostile_entity_module", () -> new ScannerModuleItem(HostileEntityScannerModule.INSTANCE));
    public static final RegistrySupplier<Item> BLOCK_MODULE = ITEMS.register("block_module", ConfigurableBlockScannerModuleItem::new);
    public static final RegistrySupplier<Item> COMMON_ORES_MODULE = ITEMS.register("common_ores_module", () -> new ScannerModuleItem(CommonOresBlockScannerModule.INSTANCE));
    public static final RegistrySupplier<Item> RARE_ORES_MODULE = ITEMS.register("rare_ores_module", () -> new ScannerModuleItem(RareOresBlockScannerModule.INSTANCE));
    public static final RegistrySupplier<Item> FLUID_MODULE = ITEMS.register("fluid_module", () -> new ScannerModuleItem(FluidBlockScannerModule.INSTANCE));

    // --------------------------------------------------------------------- //

    public static void initialize() {
        ITEMS.register();
    }
}
