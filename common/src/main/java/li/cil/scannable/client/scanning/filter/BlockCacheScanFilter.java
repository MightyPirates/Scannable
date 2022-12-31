package li.cil.scannable.client.scanning.filter;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

@Environment(EnvType.CLIENT)
public final class BlockCacheScanFilter implements Predicate<BlockState> {
    private final Collection<Block> blocks;

    public BlockCacheScanFilter(final Collection<Predicate<BlockState>> filters) {
        blocks = buildCache(filters);
    }

    public BlockCacheScanFilter(final List<Block> blocks) {
        this.blocks = new HashSet<>(blocks);
    }

    @Override
    public boolean test(final BlockState state) {
        return blocks.contains(state.getBlock());
    }

    private static Collection<Block> buildCache(final Collection<Predicate<BlockState>> filters) {
        final Set<Block> cache = new HashSet<>();
        BuiltInRegistries.BLOCK.forEach(block -> {
            final BlockState blockState = block.defaultBlockState();
            if (filters.stream().anyMatch(f -> f.test(blockState))) {
                cache.add(blockState.getBlock());
            }
        });
        return cache;
    }
}
