package li.cil.scannable.common.scanning;

import li.cil.scannable.api.scanning.ScanFilterEntity;
import li.cil.scannable.api.scanning.ScanResultProvider;
import li.cil.scannable.api.scanning.ScannerModuleEntity;
import li.cil.scannable.client.scanning.ScanResultProviders;
import li.cil.scannable.client.scanning.filter.ScanFilterEntityList;
import li.cil.scannable.client.scanning.filter.ScanFilterEntityType;
import li.cil.scannable.common.config.Settings;
import li.cil.scannable.common.item.ItemScannerModuleEntityConfigurable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public enum ScannerModuleEntityConfigurable implements ScannerModuleEntity {
    INSTANCE;

    @Override
    public int getEnergyCost(final Player player, final ItemStack module) {
        return Settings.energyCostModuleEntity;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ScanResultProvider getResultProvider() {
        return ScanResultProviders.ENTITIES.get();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Optional<ScanFilterEntity> getFilter(final ItemStack module) {
        final List<EntityType<?>> entityType = ItemScannerModuleEntityConfigurable.getEntityTypes(module);
        return Optional.of(new ScanFilterEntityList(entityType.stream().map(ScanFilterEntityType::new).collect(Collectors.toList())));
    }
}
