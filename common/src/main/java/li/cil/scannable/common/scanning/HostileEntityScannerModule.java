/* SPDX-License-Identifier: MIT */

package li.cil.scannable.common.scanning;

import li.cil.scannable.api.API;
import li.cil.scannable.api.scanning.EntityScannerModule;
import li.cil.scannable.api.scanning.ScanResultProvider;
import li.cil.scannable.client.scanning.ScanResultProviders;
import li.cil.scannable.client.scanning.filter.HostileEntityScanFilter;
import li.cil.scannable.common.config.ServerConfig;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.function.Predicate;

public enum HostileEntityScannerModule implements EntityScannerModule {
    INSTANCE;

    @Override
    public int getEnergyCost(final ItemStack module) {
        return ServerConfig.energyCostModuleMonster;
    }

    @Override
    public float adjustLocalRange(final float range) {
        return range * ServerConfig.rangeModifierModuleMonster;
    }

    @Override
    public ScanResultProvider getResultProvider() {
        return ScanResultProviders.entities();
    }

    @Override
    public Optional<Identifier> getIcon(final Entity entity) {
        return Optional.of(API.ICON_WARNING);
    }

    @Override
    public Predicate<Entity> getFilter(final ItemStack module) {
        return HostileEntityScanFilter.INSTANCE;
    }
}
