package li.cil.scannable.common.neoforge.capabilities;

import li.cil.scannable.api.API;
import li.cil.scannable.common.config.CommonConfig;
import li.cil.scannable.common.energy.neoforge.ScannerEnergyStorage;
import li.cil.scannable.common.inventory.ScannerContainer;
import li.cil.scannable.common.item.Items;
import li.cil.scannable.common.item.ScannerModuleItem;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;

@EventBusSubscriber(modid = API.MOD_ID)
public final class ModCapabilities {
    public static final class ScannerModule {
        public static final ItemCapability<li.cil.scannable.api.scanning.ScannerModule, Void> ITEM = ItemCapability.createVoid(Identifier.fromNamespaceAndPath(API.MOD_ID, "scanner_module"), li.cil.scannable.api.scanning.ScannerModule.class);
    }

    // --------------------------------------------------------------------- //

    @SubscribeEvent
    public static void initialize(final RegisterCapabilitiesEvent event) {
        event.registerItem(Capabilities.Item.ITEM,
            (stack, context) -> VanillaContainerWrapper.of(ScannerContainer.of(stack)),
            Items.SCANNER.get());
        event.registerItem(Capabilities.Energy.ITEM,
            (stack, context) -> CommonConfig.useEnergy ? ScannerEnergyStorage.of(stack) : null,
            Items.SCANNER.get());
        event.registerItem(ScannerModule.ITEM, (stack, context) -> ((ScannerModuleItem) stack.getItem()).getModule(),
            Items.RANGE_MODULE.get(),
            Items.ENTITY_MODULE.get(),
            Items.FRIENDLY_ENTITY_MODULE.get(),
            Items.HOSTILE_ENTITY_MODULE.get(),
            Items.BLOCK_MODULE.get(),
            Items.COMMON_ORES_MODULE.get(),
            Items.RARE_ORES_MODULE.get(),
            Items.FLUID_MODULE.get(),
            Items.CHEST_MODULE.get());
    }

    // --------------------------------------------------------------------- //

    private ModCapabilities() {
    }
}
