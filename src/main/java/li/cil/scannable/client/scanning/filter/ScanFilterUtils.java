package li.cil.scannable.client.scanning.filter;

import li.cil.scannable.api.API;
import li.cil.scannable.api.scanning.ScanFilterBlock;
import li.cil.scannable.common.config.Settings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = API.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public enum ScanFilterUtils {
    INSTANCE;

    @OnlyIn(Dist.CLIENT)
    private ScanFilterBlock filter;

    public static boolean shouldIgnore(final BlockState state) {
        INSTANCE.validateFilter();
        return INSTANCE.filter.matches(state);
    }

    @OnlyIn(Dist.CLIENT)
    private void validateFilter() {
        if (filter != null) {
            return;
        }

        final List<ScanFilterBlock> filters = new ArrayList<>();
        for (final ResourceLocation location : Settings.ignoredBlocks) {
            final Block block = ForgeRegistries.BLOCKS.getValue(location);
            if (block != null) {
                filters.add(new ScanFilterSingleBlock(block));
            }
        }
        for (final ResourceLocation location : Settings.ignoredBlockTags) {
            final ITag<Block> tag = BlockTags.getCollection().get(location);
            if (tag != null) {
                filters.add(new ScanFilterBlockTag(tag));
            }
        }
        filter = new ScanFilterBlockCache(filters);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onModConfigEvent(final ModConfig.ModConfigEvent configEvent) {
        // Reset on any config change so we also rebuild the filter when resource reload
        // kicks in which can result in ids changing and thus our cache being invalid.
        ScanFilterUtils.INSTANCE.filter = null;
    }
}
