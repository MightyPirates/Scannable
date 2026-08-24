/* SPDX-License-Identifier: MIT */

package li.cil.scannable.common.scanning;

import li.cil.scannable.api.scanning.BlockScannerModule;
import li.cil.scannable.api.scanning.ScanResultProvider;
import li.cil.scannable.client.scanning.ScanResultProviders;
import li.cil.scannable.client.scanning.filter.BlockCacheScanFilter;
import li.cil.scannable.client.scanning.filter.BlockScanFilter;
import li.cil.scannable.client.scanning.filter.BlockTagScanFilter;
import li.cil.scannable.common.config.ServerConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public enum ChestScannerModule implements BlockScannerModule {
    INSTANCE;

    private Predicate<BlockState> filter;

    public static void clearCache() {
        INSTANCE.filter = null;
    }

    @Override
    public int getEnergyCost(final ItemStack module) {
        return ServerConfig.energyCostModuleChest;
    }

    @Override
    public ScanResultProvider getResultProvider() {
        return ScanResultProviders.blocks();
    }

    @Override
    public float adjustLocalRange(final float range) {
        return range * ServerConfig.rangeModifierModuleChest;
    }

    @Override
    public Predicate<BlockState> getFilter(final ItemStack module) {
        validateFilter();
        return filter;
    }

    private void validateFilter() {
        if (filter != null) {
            return;
        }

        final List<Predicate<BlockState>> filters = new ArrayList<>();
        for (final Identifier location : ServerConfig.commonChestBlocks) {
            BuiltInRegistries.BLOCK.getOptional(location).ifPresent(block ->
                filters.add(new BlockScanFilter(block)));
        }
        BuiltInRegistries.BLOCK.getTags().forEach(namedTag -> {
            final TagKey<Block> tag = namedTag.key();
            if (ServerConfig.commonChestTags.contains(tag.location())) {
                filters.add(new BlockTagScanFilter(tag));
            }
        });
        filter = new BlockCacheScanFilter(filters);
    }
}
