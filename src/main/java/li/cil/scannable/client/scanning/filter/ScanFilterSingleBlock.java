package li.cil.scannable.client.scanning.filter;

import li.cil.scannable.api.scanning.ScanFilterBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

public final class ScanFilterSingleBlock implements ScanFilterBlock {
    private final Block block;

    public ScanFilterSingleBlock(final Block block) {
        this.block = block;
    }

    public boolean matches(final BlockState state) {
        return block.getBlock() == state.getBlock();
    }
}
