package li.cil.scannable.common.scanning;

import li.cil.scannable.api.scanning.EntityScannerModule;
import li.cil.scannable.api.scanning.ScanResultProvider;
import li.cil.scannable.client.scanning.ScanResultProviders;
import li.cil.scannable.client.scanning.filter.EntityListScanFilter;
import li.cil.scannable.client.scanning.filter.EntityTypeScanFilter;
import li.cil.scannable.common.config.CommonConfig;
import li.cil.scannable.common.item.ConfigurableEntityScannerModuleItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public enum ConfigurableEntityScannerModule implements EntityScannerModule {
    INSTANCE;

    @Override
    public int getEnergyCost(final ItemStack module) {
        return CommonConfig.energyCostModuleEntity;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public ScanResultProvider getResultProvider() {
        return ScanResultProviders.ENTITIES.get();
    }

    @Environment(EnvType.CLIENT)
    @Override
    public Predicate<Entity> getFilter(final ItemStack module) {
        final List<EntityType<?>> entityType = ConfigurableEntityScannerModuleItem.getEntityTypes(module);
        return new EntityListScanFilter(entityType.stream().map(EntityTypeScanFilter::new).collect(Collectors.toList()));
    }
}
