/* SPDX-License-Identifier: MIT */

package li.cil.scannable.common.neoforge.capabilities;

import li.cil.scannable.api.API;
import li.cil.scannable.common.config.CommonConfig;
import li.cil.scannable.common.energy.neoforge.ScannerEnergyStorage;
import li.cil.scannable.common.inventory.ScannerContainer;
import li.cil.scannable.common.item.Items;
import li.cil.scannable.common.item.ScannerModuleItem;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

@EventBusSubscriber(modid = API.MOD_ID)
public final class Capabilities {
    public static final class ScannerModule {
        public static final ItemCapability<li.cil.scannable.api.scanning.ScannerModule, Void> ITEM = ItemCapability.createVoid(ResourceLocation.fromNamespaceAndPath(API.MOD_ID, "scanner_module"), li.cil.scannable.api.scanning.ScannerModule.class);
    }

    // --------------------------------------------------------------------- //

    @SubscribeEvent
    public static void initialize(final RegisterCapabilitiesEvent event) {
        event.registerItem(ItemHandler.ITEM, (stack, context) -> new InvWrapper(ScannerContainer.of(stack)),
            Items.SCANNER.get());
        event.registerItem(EnergyStorage.ITEM,
            (stack, context) -> CommonConfig.useEnergy ? new ScannerEnergyStorage(stack) : null,
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
}
