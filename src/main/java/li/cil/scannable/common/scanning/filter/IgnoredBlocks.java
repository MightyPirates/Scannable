package li.cil.scannable.common.scanning.filter;

import li.cil.scannable.api.API;
import li.cil.scannable.common.config.CommonConfig;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITagManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mod.EventBusSubscriber(modid = API.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
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
            final Block block = ForgeRegistries.BLOCKS.getValue(location);
            if (block != null) {
                ignoredBlocks.add(block);
            }
        }

        final List<TagKey<Block>> ignoredTags = new ArrayList<>();
        final ITagManager<Block> tags = ForgeRegistries.BLOCKS.tags();
        if (tags != null) {
            for (final ResourceLocation location : CommonConfig.ignoredBlockTags) {
                final TagKey<Block> tag = TagKey.create(Registry.BLOCK_REGISTRY, location);
                if (tags.isKnownTagName(tag)) {
                    ignoredTags.add(tag);
                }
            }
        }

        for (final Block block : ForgeRegistries.BLOCKS.getValues()) {
            final BlockState blockState = block.defaultBlockState();
            if (ignoredTags.stream().anyMatch(blockState::is)) {
                ignoredBlocks.add(blockState.getBlock());
            }
        }

        this.ignoredBlocks = ignoredBlocks;
    }

    @SubscribeEvent
    public static void onModConfigEvent(final ModConfigEvent configEvent) {
        // Reset on any config change so we also rebuild the filter when resource reload
        // kicks in which can result in ids changing and thus our cache being invalid.
        IgnoredBlocks.INSTANCE.ignoredBlocks = null;
    }
}
