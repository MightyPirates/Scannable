package li.cil.scannable.common.scanning;

import li.cil.scannable.api.API;
import li.cil.scannable.api.scanning.ScanFilterBlock;
import li.cil.scannable.api.scanning.ScanResultProvider;
import li.cil.scannable.api.scanning.ScannerModuleBlock;
import li.cil.scannable.client.scanning.filter.ScanFilterBlockCache;
import li.cil.scannable.client.scanning.filter.ScanFilterBlockTag;
import li.cil.scannable.client.scanning.filter.ScanFilterRareOreCatchAll;
import li.cil.scannable.client.scanning.filter.ScanFilterSingleBlock;
import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.config.Settings;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mod.EventBusSubscriber(modid = API.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public enum ScannerModuleOreRare implements ScannerModuleBlock {
    INSTANCE;

    @OnlyIn(Dist.CLIENT)
    private ScanFilterBlock filter;

    @Override
    public int getEnergyCost(final PlayerEntity player, final ItemStack module) {
        return Settings.energyCostModuleOreCommon;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ScanResultProvider getResultProvider() {
        return GameRegistry.findRegistry(ScanResultProvider.class).getValue(API.SCAN_RESULT_PROVIDER_BLOCKS);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float adjustLocalRange(final float range) {
        return range * Constants.MODULE_ORE_RADIUS_MULTIPLIER;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Optional<ScanFilterBlock> getFilter(final ItemStack module) {
        validateFilter();
        return Optional.of(filter);
    }

    @OnlyIn(Dist.CLIENT)
    private void validateFilter() {
        if (filter != null) {
            return;
        }

        final List<ScanFilterBlock> filters = new ArrayList<>();
        for (final ResourceLocation location : Settings.rareOreBlocks) {
            final Block block = ForgeRegistries.BLOCKS.getValue(location);
            if (block != null) {
                filters.add(new ScanFilterSingleBlock(block));
            }
        }
        for (final ResourceLocation location : Settings.rareOreBlockTags) {
            final ITag<Block> tag = BlockTags.getAllTags().getTag(location);
            if (tag != null) {
                filters.add(new ScanFilterBlockTag(tag));
            }
        }
        filters.add(ScanFilterRareOreCatchAll.INSTANCE);
        filter = new ScanFilterBlockCache(filters);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onModConfigEvent(final ModConfig.ModConfigEvent configEvent) {
        // Reset on any config change so we also rebuild the filter when resource reload
        // kicks in which can result in ids changing and thus our cache being invalid.
        ScannerModuleOreRare.INSTANCE.filter = null;
    }
}
