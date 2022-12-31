package li.cil.scannable.common.scanning.filter;

import li.cil.scannable.common.config.CommonConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public enum IgnoredBlocks {
    INSTANCE;

    private Set<Block> ignoredBlocks;

    public static void clearCache() {
        INSTANCE.ignoredBlocks = null;
    }

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
            BuiltInRegistries.BLOCK.getOptional(location).ifPresent(ignoredBlocks::add);
        }

        final List<TagKey<Block>> ignoredTags = new ArrayList<>();
        BuiltInRegistries.BLOCK.getTagNames().forEach(namedTag -> {
            if (CommonConfig.ignoredBlockTags.contains(namedTag.location())) {
                ignoredTags.add(namedTag);
            }
        });

        for (final Block block : BuiltInRegistries.BLOCK) {
            final BlockState blockState = block.defaultBlockState();
            if (ignoredTags.stream().anyMatch(blockState::is)) {
                ignoredBlocks.add(blockState.getBlock());
            }
        }

        this.ignoredBlocks = ignoredBlocks;
    }
}
