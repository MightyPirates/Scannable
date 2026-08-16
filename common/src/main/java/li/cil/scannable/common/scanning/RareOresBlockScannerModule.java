package li.cil.scannable.common.scanning;

import li.cil.scannable.api.scanning.BlockScannerModule;
import li.cil.scannable.api.scanning.ScanResultProvider;
import li.cil.scannable.client.scanning.ScanResultProviders;
import li.cil.scannable.client.scanning.filter.BlockCacheScanFilter;
import li.cil.scannable.client.scanning.filter.BlockScanFilter;
import li.cil.scannable.client.scanning.filter.BlockTagScanFilter;
import li.cil.scannable.common.config.CommonConfig;
import li.cil.scannable.common.scanning.filter.IgnoredBlocks;
import li.cil.scannable.common.tags.CommonTags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public enum RareOresBlockScannerModule implements BlockScannerModule {
    INSTANCE;

    private Predicate<BlockState> filter;

    public static void clearCache() {
        INSTANCE.filter = null;
    }

    @Override
    public int getEnergyCost(final ItemStack module) {
        return CommonConfig.energyCostModuleOreRare;
    }

    @Override
    public ScanResultProvider getResultProvider() {
        return ScanResultProviders.blocks();
    }

    @Override
    public float adjustLocalRange(final float range) {
        return range * CommonConfig.rangeModifierModuleOreRare;
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
        for (final Identifier location : CommonConfig.rareOreBlocks) {
            BuiltInRegistries.BLOCK.getOptional(location).ifPresent(block ->
                filters.add(new BlockScanFilter(block)));
        }
        BuiltInRegistries.BLOCK.getTags().forEach(namedTag -> {
            final TagKey<Block> tag = namedTag.key();
            if (CommonConfig.rareOreBlockTags.contains(tag.location())) {
                filters.add(new BlockTagScanFilter(tag));
            }
        });

        // Treat all blocks tagged as ores but not part of the common ore category as rare.
        final TagKey<Block> topLevelOreTag = getTopLevelOreTag();
        filters.add(state -> !IgnoredBlocks.contains(state) &&
            state.is(topLevelOreTag) &&
            !CommonOresBlockScannerModule.INSTANCE.getFilter(ItemStack.EMPTY).test(state));

        filter = new BlockCacheScanFilter(filters);
    }

    private static TagKey<Block> getTopLevelOreTag() {
        return CommonTags.ORES;
    }
}
