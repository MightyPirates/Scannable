/* SPDX-License-Identifier: MIT */

package li.cil.scannable.common.fabric;

import li.cil.scannable.common.CommonSetup;
import li.cil.scannable.common.config.CommonConfig;
import li.cil.scannable.common.item.Items;
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
    }
}
