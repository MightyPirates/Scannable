package li.cil.scannable.common.scanning;

import li.cil.scannable.api.API;
import li.cil.scannable.api.scanning.EntityScannerModule;
import li.cil.scannable.api.scanning.ScanResultProvider;
import li.cil.scannable.client.scanning.ScanResultProviders;
import li.cil.scannable.client.scanning.filter.HostileEntityScanFilter;
import li.cil.scannable.common.config.CommonConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.function.Predicate;

public enum HostileEntityScannerModule implements EntityScannerModule {
    INSTANCE;

    @Override
    public int getEnergyCost(final ItemStack module) {
        return CommonConfig.energyCostModuleMonster;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public ScanResultProvider getResultProvider() {
        return ScanResultProviders.ENTITIES.get();
    }

    @Environment(EnvType.CLIENT)
    @Override
    public Optional<ResourceLocation> getIcon(final Entity entity) {
        return Optional.of(API.ICON_WARNING);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public Predicate<Entity> getFilter(final ItemStack module) {
        return HostileEntityScanFilter.INSTANCE;
    }
}
