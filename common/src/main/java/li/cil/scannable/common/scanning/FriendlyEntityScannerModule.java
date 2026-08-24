/* SPDX-License-Identifier: MIT */

package li.cil.scannable.common.scanning;

import li.cil.scannable.api.scanning.EntityScannerModule;
import li.cil.scannable.api.scanning.ScanResultProvider;
import li.cil.scannable.client.scanning.ScanResultProviders;
import li.cil.scannable.client.scanning.filter.FriendlyEntityScanFilter;
import li.cil.scannable.common.config.ServerConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public enum FriendlyEntityScannerModule implements EntityScannerModule {
    INSTANCE;

    @Override
    public int getEnergyCost(final ItemStack module) {
        return ServerConfig.energyCostModuleAnimal;
    }

    @Override
    public float adjustLocalRange(final float range) {
        return range * ServerConfig.rangeModifierModuleAnimal;
    }

    @Override
    public ScanResultProvider getResultProvider() {
        return ScanResultProviders.entities();
    }

    @Override
    public Predicate<Entity> getFilter(final ItemStack module) {
        return FriendlyEntityScanFilter.INSTANCE;
    }
}
