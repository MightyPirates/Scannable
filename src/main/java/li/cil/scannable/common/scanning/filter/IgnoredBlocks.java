package li.cil.scannable.common.scanning.filter;

import li.cil.scannable.common.config.CommonConfig;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.fml.event.config.ModConfigEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public enum IgnoredBlocks {
    INSTANCE;

    private Set<Block> ignoredBlocks;

    public static boolean contains(final BlockState state) {
        INSTANCE.validateFilter();
        return INSTANCE.ignoredBlocks.contains(state.getBlock());
    }

    private void validateFilter() {
        if (ignoredBlocks != null) {
            return;
        }

        final Set<Block> ignoredBlocks = new HashSet<>();
        for (final ResourceLocation location : CommonConfig.ignoredBlocks) {
            final Block block = Registry.BLOCK.getOptional(location).orElse(null);
            if (block != null) {
                ignoredBlocks.add(block);
            }
        }

        final List<Tag<Block>> ignoredTags = new ArrayList<>();
        for (final ResourceLocation location : CommonConfig.ignoredBlockTags) {
            final Tag<Block> tag = BlockTags.getAllTags().getTag(location);
            if (tag != null) {
                ignoredTags.add(tag);
            }
        }

        for (final Block block : Registry.BLOCK) {
            final BlockState blockState = block.defaultBlockState();
            if (ignoredTags.stream().anyMatch(tag -> tag.contains(block))) {
                ignoredBlocks.add(blockState.getBlock());
            }
        }

        this.ignoredBlocks = ignoredBlocks;
    }

    static {
        ModConfigEvent.LOADING.register((cfg) -> IgnoredBlocks.INSTANCE.ignoredBlocks = null);
        ModConfigEvent.RELOADING.register((cfg) -> IgnoredBlocks.INSTANCE.ignoredBlocks = null);
    }
}
