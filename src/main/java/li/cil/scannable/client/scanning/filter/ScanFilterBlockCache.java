package li.cil.scannable.client.scanning.filter;

import li.cil.scannable.api.scanning.ScanFilterBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ScanFilterBlockCache implements ScanFilterBlock {
    private final Collection<Block> blocks;

    public ScanFilterBlockCache(final Collection<ScanFilterBlock> filters) {
        blocks = buildCache(filters);
    }

    public ScanFilterBlockCache(final List<Block> blocks) {
        this.blocks = new HashSet<>(blocks);
    }

    @Override
    public boolean matches(final BlockState state) {
        return blocks.contains(state.getBlock());
    }

    private static Collection<Block> buildCache(final Collection<ScanFilterBlock> filters) {
        final Set<Block> cache = new HashSet<>();
        for (final Block block : ForgeRegistries.BLOCKS.getValues()) {
            final BlockState blockState = block.getDefaultState();
            if (filters.stream().anyMatch(f -> f.matches(blockState))) {
                cache.add(blockState.getBlock());
            }
        }
        return cache;
    }
}
