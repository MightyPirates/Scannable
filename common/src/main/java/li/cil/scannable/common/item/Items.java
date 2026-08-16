package li.cil.scannable.common.item;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import li.cil.scannable.api.API;
import li.cil.scannable.api.scanning.ScannerModule;
import li.cil.scannable.common.scanning.*;
import li.cil.scannable.util.RegistryUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public final class Items {
    private static final DeferredRegister<Item> ITEMS = RegistryUtils.get(Registries.ITEM);

    // --------------------------------------------------------------------- //

    public static final RegistrySupplier<Item> SCANNER = register("scanner", ScannerItem::new);

    public static final RegistrySupplier<Item> BLANK_MODULE = register("blank_module", ModItem::new);
    public static final RegistrySupplier<Item> RANGE_MODULE = registerModule("range_module", RangeScannerModule.INSTANCE);
    public static final RegistrySupplier<Item> ENTITY_MODULE = register("entity_module", ConfigurableEntityScannerModuleItem::new);
    public static final RegistrySupplier<Item> FRIENDLY_ENTITY_MODULE = registerModule("friendly_entity_module", FriendlyEntityScannerModule.INSTANCE);
    public static final RegistrySupplier<Item> HOSTILE_ENTITY_MODULE = registerModule("hostile_entity_module", HostileEntityScannerModule.INSTANCE);
    public static final RegistrySupplier<Item> BLOCK_MODULE = register("block_module", ConfigurableBlockScannerModuleItem::new);
    public static final RegistrySupplier<Item> COMMON_ORES_MODULE = registerModule("common_ores_module", CommonOresBlockScannerModule.INSTANCE);
    public static final RegistrySupplier<Item> RARE_ORES_MODULE = registerModule("rare_ores_module", RareOresBlockScannerModule.INSTANCE);
    public static final RegistrySupplier<Item> FLUID_MODULE = registerModule("fluid_module", FluidBlockScannerModule.INSTANCE);
    public static final RegistrySupplier<Item> CHEST_MODULE = registerModule("chest_module", ChestScannerModule.INSTANCE);

    // --------------------------------------------------------------------- //

    public static void initialize() {
    }

    // --------------------------------------------------------------------- //

    private static RegistrySupplier<Item> registerModule(final String name, final ScannerModule module) {
        return register(name, properties -> new ScannerModuleItem(properties, module));
    }

    private static RegistrySupplier<Item> register(final String name, final Function<Item.Properties, Item> factory) {
        return ITEMS.register(name, () -> factory.apply(new Item.Properties()
            .setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(API.MOD_ID, name)))));
    }

    private Items() {
    }
}
