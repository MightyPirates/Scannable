package li.cil.scannable.common.scanning;

import li.cil.scannable.api.API;
import li.cil.scannable.api.scanning.ScanFilterEntity;
import li.cil.scannable.api.scanning.ScanResultProvider;
import li.cil.scannable.api.scanning.ScannerModuleEntity;
import li.cil.scannable.client.scanning.filter.ScanFilterEntityList;
import li.cil.scannable.client.scanning.filter.ScanFilterEntityType;
import li.cil.scannable.common.config.Settings;
import li.cil.scannable.common.item.ItemScannerModuleEntityConfigurable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public enum ScannerModuleEntityConfigurable implements ScannerModuleEntity {
    INSTANCE;

    @Override
    public int getEnergyCost(final PlayerEntity player, final ItemStack module) {
        return Settings.energyCostModuleEntity;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ScanResultProvider getResultProvider() {
        return GameRegistry.findRegistry(ScanResultProvider.class).getValue(API.SCAN_RESULT_PROVIDER_ENTITIES);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Optional<ScanFilterEntity> getFilter(final ItemStack module) {
        final List<EntityType<?>> entityType = ItemScannerModuleEntityConfigurable.getEntityTypes(module);
        return Optional.of(new ScanFilterEntityList(entityType.stream().map(ScanFilterEntityType::new).collect(Collectors.toList())));
    }
}
