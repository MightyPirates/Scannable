package li.cil.scannable.common.scanning.filter;

import li.cil.scannable.common.config.CommonConfig;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
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
            Registry.BLOCK.getOptional(location).ifPresent(ignoredBlocks::add);
        }

        final List<TagKey<Block>> ignoredTags = new ArrayList<>();
        Registry.BLOCK.getTagNames().forEach(namedTag -> {
            if (CommonConfig.ignoredBlockTags.contains(namedTag.location())) {
                ignoredTags.add(namedTag);
            }
        });

        for (final Block block : Registry.BLOCK) {
            final BlockState blockState = block.defaultBlockState();
            if (ignoredTags.stream().anyMatch(blockState::is)) {
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
