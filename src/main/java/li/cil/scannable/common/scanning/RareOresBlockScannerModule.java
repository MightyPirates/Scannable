package li.cil.scannable.common.scanning;

import li.cil.scannable.api.scanning.BlockScannerModule;
import li.cil.scannable.api.scanning.ScanResultProvider;
import li.cil.scannable.client.scanning.ScanResultProviders;
import li.cil.scannable.client.scanning.filter.BlockCacheScanFilter;
import li.cil.scannable.client.scanning.filter.BlockScanFilter;
import li.cil.scannable.client.scanning.filter.BlockTagScanFilter;
import li.cil.scannable.common.config.CommonConfig;
import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.scanning.filter.IgnoredBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.fml.event.config.ModConfigEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public enum RareOresBlockScannerModule implements BlockScannerModule {
    INSTANCE;

    private Predicate<BlockState> filter;

    @Override
    public int getEnergyCost(final ItemStack module) {
        return CommonConfig.energyCostModuleOreRare;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public ScanResultProvider getResultProvider() {
        return ScanResultProviders.BLOCKS.get();
    }

    @Environment(EnvType.CLIENT)
    @Override
    public float adjustLocalRange(final float range) {
        return range * Constants.ORE_MODULE_RADIUS_MULTIPLIER;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public Predicate<BlockState> getFilter(final ItemStack module) {
        validateFilter();
        return filter;
    }

    @Environment(EnvType.CLIENT)
    private void validateFilter() {
        if (filter != null) {
            return;
        }

        final List<Predicate<BlockState>> filters = new ArrayList<>();
        for (final ResourceLocation location : CommonConfig.rareOreBlocks) {
            Registry.BLOCK.getOptional(location).ifPresent(block ->
                filters.add(new BlockScanFilter(block)));
        }
        Registry.BLOCK.getTagNames().forEach(tag -> {
            if (CommonConfig.rareOreBlockTags.contains(tag.location())) {
                filters.add(new BlockTagScanFilter(tag));
            }
        });

        // Treat all blocks tagged as ores but not part of the common ore category as rare.
        filters.add(state -> !IgnoredBlocks.contains(state) &&
                             state.is(TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation("c", "ores"))) &&
                             !CommonOresBlockScannerModule.INSTANCE.getFilter(ItemStack.EMPTY).test(state));

        filter = new BlockCacheScanFilter(filters);
    }

    static {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ModConfigEvent.LOADING.register((cfg) -> RareOresBlockScannerModule.INSTANCE.filter = null);
            ModConfigEvent.RELOADING.register((cfg) -> RareOresBlockScannerModule.INSTANCE.filter = null);
        }
    }
}
