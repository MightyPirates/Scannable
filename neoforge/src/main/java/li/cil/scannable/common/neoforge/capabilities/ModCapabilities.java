package li.cil.scannable.common.neoforge.capabilities;

import li.cil.scannable.api.API;
import li.cil.scannable.common.config.CommonConfig;
import li.cil.scannable.common.inventory.ScannerContainer;
import li.cil.scannable.common.item.Items;
import li.cil.scannable.common.item.ModDataComponents;
import li.cil.scannable.common.item.ScannerModuleItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.transfer.energy.ItemAccessEnergyHandler;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;

@EventBusSubscriber(modid = API.MOD_ID)
public final class ModCapabilities {
    @SubscribeEvent
    public static void initialize(final RegisterCapabilitiesEvent event) {
        event.registerItem(Capabilities.Item.ITEM,
            (stack, context) -> VanillaContainerWrapper.of(ScannerContainer.of(stack)),
            Items.SCANNER.get());
        event.registerItem(Capabilities.Energy.ITEM,
            (stack, access) -> CommonConfig.useEnergy
                ? new ItemAccessEnergyHandler(access, ModDataComponents.ENERGY.get(), CommonConfig.energyCapacityScanner)
                : null,
            Items.SCANNER.get());
        event.registerItem(li.cil.scannable.api.neoforge.Capabilities.SCANNER_MODULE,
            (stack, context) -> ((ScannerModuleItem) stack.getItem()).getModule(),
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
