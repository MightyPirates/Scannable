package li.cil.scannable.client.scanning.filter;

import it.unimi.dsi.fastutil.ints.*;
import li.cil.scannable.api.scanning.ScanFilterBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collection;
import java.util.List;

public final class ScanFilterBlockCache implements ScanFilterBlock {
    private final IntSet stateIds;

    public ScanFilterBlockCache(final Collection<ScanFilterBlock> filters) {
        stateIds = buildCache(filters);
    }

    public ScanFilterBlockCache(final List<Block> blocks) {
        stateIds = buildCache(blocks);
    }

    @Override
    public boolean matches(final BlockState state) {
        return stateIds.contains(Block.getStateId(state));
    }

    private static IntSet buildCache(final Collection<ScanFilterBlock> filters) {
        final IntList list = new IntArrayList();
        for (final Block block : ForgeRegistries.BLOCKS.getValues()) {
            for (final BlockState blockState : block.getStateContainer().getValidStates()) {
                if (filters.stream().anyMatch(f -> f.matches(blockState))) {
                    list.add(Block.getStateId(blockState));
                }
            }
        }

        if (list.size() > 10) {
            return new IntOpenHashSet(list);
        } else {
            return new IntArraySet(list);
        }
    }

    private static IntSet buildCache(final List<Block> blocks) {
        final IntList list = new IntArrayList();

        for (final Block block : blocks) {
            for (final BlockState blockState : block.getStateContainer().getValidStates()) {
                list.add(Block.getStateId(blockState));
            }
        }

        if (list.size() > 10) {
            return new IntOpenHashSet(list);
        } else {
            return new IntArraySet(list);
        }
    }
}
