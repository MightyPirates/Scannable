package li.cil.scannable.client.scanning.filter;

import li.cil.scannable.api.scanning.ScanFilterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public record ScanFilterSingleBlock(Block block) implements ScanFilterBlock {
    @Override
    public boolean matches(final BlockState state) {
        return block == state.getBlock();
    }
}
