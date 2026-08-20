package li.cil.scannable.common.fabric;

import li.cil.scannable.api.fabric.Lookups;
import li.cil.scannable.common.CommonSetup;
import li.cil.scannable.common.config.CommonConfig;
import li.cil.scannable.common.item.Items;
import li.cil.scannable.common.item.ScannerModuleItem;
import net.fabricmc.api.ModInitializer;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyItem;

public final class CommonSetupFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        CommonSetup.initialize();

        EnergyStorage.ITEM.registerForItems((stack, context) -> {
            if (!CommonConfig.useEnergy) {
                return null;
            }

            return SimpleEnergyItem.createStorage(context,
                CommonConfig.energyCapacityScanner,
                Long.MAX_VALUE,
                Long.MAX_VALUE);
        }, Items.SCANNER.get());

        Lookups.SCANNER_MODULE.registerForItems((stack, context) -> ((ScannerModuleItem) stack.getItem()).getModule(),
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
