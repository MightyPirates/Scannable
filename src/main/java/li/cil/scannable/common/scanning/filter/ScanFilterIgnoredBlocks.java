package li.cil.scannable.common.scanning.filter;

import li.cil.scannable.api.API;
import li.cil.scannable.common.config.Settings;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mod.EventBusSubscriber(modid = API.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public enum ScanFilterIgnoredBlocks {
    INSTANCE;

    private Set<Block> filter;

    public static boolean shouldIgnore(final BlockState state) {
        INSTANCE.validateFilter();
        return INSTANCE.filter.contains(state.getBlock());
    }

    private void validateFilter() {
        if (filter != null) {
            return;
        }

        final Set<Block> ignoredBlocks = new HashSet<>();
        for (final ResourceLocation location : Settings.ignoredBlocks) {
            final Block block = ForgeRegistries.BLOCKS.getValue(location);
            if (block != null) {
                ignoredBlocks.add(block);
            }
        }

        final List<Tag<Block>> ignoredTags = new ArrayList<>();
        for (final ResourceLocation location : Settings.ignoredBlockTags) {
            final Tag<Block> tag = BlockTags.getAllTags().getTag(location);
            if (tag != null) {
                ignoredTags.add(tag);
            }
        }

        for (final Block block : ForgeRegistries.BLOCKS.getValues()) {
            final BlockState blockState = block.defaultBlockState();
            if (ignoredTags.stream().anyMatch(tag -> tag.contains(block))) {
                ignoredBlocks.add(blockState.getBlock());
            }
        }

        filter = ignoredBlocks;
    }

    @SubscribeEvent
    public static void onModConfigEvent(final ModConfigEvent configEvent) {
        // Reset on any config change so we also rebuild the filter when resource reload
        // kicks in which can result in ids changing and thus our cache being invalid.
        ScanFilterIgnoredBlocks.INSTANCE.filter = null;
    }
}
