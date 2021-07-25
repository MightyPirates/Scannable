package li.cil.scannable.common.scanning;

import li.cil.scannable.api.API;
import li.cil.scannable.api.scanning.BlockScannerModule;
import li.cil.scannable.api.scanning.ScanResultProvider;
import li.cil.scannable.client.scanning.ScanResultProviders;
import li.cil.scannable.client.scanning.filter.BlockCacheScanFilter;
import li.cil.scannable.client.scanning.filter.BlockTagScanFilter;
import li.cil.scannable.client.scanning.filter.BlockScanFilter;
import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.config.Settings;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Mod.EventBusSubscriber(modid = API.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public enum CommonOreBlockScannerModule implements BlockScannerModule {
    INSTANCE;

    private Predicate<BlockState> filter;

    @Override
    public int getEnergyCost(final Player player, final ItemStack module) {
        return Settings.energyCostModuleOreCommon;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ScanResultProvider getResultProvider() {
        return ScanResultProviders.BLOCKS.get();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float adjustLocalRange(final float range) {
        return range * Constants.MODULE_ORE_RADIUS_MULTIPLIER;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Predicate<BlockState> getFilter(final ItemStack module) {
        validateFilter();
        return filter;
    }

    @OnlyIn(Dist.CLIENT)
    private void validateFilter() {
        if (filter != null) {
            return;
        }

        final List<Predicate<BlockState>> filters = new ArrayList<>();
        for (final ResourceLocation location : Settings.commonOreBlocks) {
            final Block block = ForgeRegistries.BLOCKS.getValue(location);
            if (block != null) {
                filters.add(new BlockScanFilter(block));
            }
        }
        for (final ResourceLocation location : Settings.commonOreBlockTags) {
            final Tag<Block> tag = BlockTags.getAllTags().getTag(location);
            if (tag != null) {
                filters.add(new BlockTagScanFilter(tag));
            }
        }
        filter = new BlockCacheScanFilter(filters);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onModConfigEvent(final ModConfigEvent configEvent) {
        // Reset on any config change so we also rebuild the filter when resource reload
        // kicks in which can result in ids changing and thus our cache being invalid.
        CommonOreBlockScannerModule.INSTANCE.filter = null;
    }
}
