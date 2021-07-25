package li.cil.scannable.common.scanning;

import li.cil.scannable.api.scanning.ScanResultProvider;
import li.cil.scannable.api.scanning.EntityScannerModule;
import li.cil.scannable.client.scanning.ScanResultProviders;
import li.cil.scannable.client.scanning.filter.EntityListScanFilter;
import li.cil.scannable.client.scanning.filter.EntityTypeScanFilter;
import li.cil.scannable.common.config.Settings;
import li.cil.scannable.common.item.ConfigurableEntityScannerModuleItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public enum ConfigurableEntityScannerModule implements EntityScannerModule {
    INSTANCE;

    @Override
    public int getEnergyCost(final ItemStack module) {
        return Settings.energyCostModuleEntity;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ScanResultProvider getResultProvider() {
        return ScanResultProviders.ENTITIES.get();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Predicate<Entity> getFilter(final ItemStack module) {
        final List<EntityType<?>> entityType = ConfigurableEntityScannerModuleItem.getEntityTypes(module);
        return new EntityListScanFilter(entityType.stream().map(EntityTypeScanFilter::new).collect(Collectors.toList()));
    }
}
