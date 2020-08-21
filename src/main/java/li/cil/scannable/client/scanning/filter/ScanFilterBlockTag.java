package li.cil.scannable.client.scanning.filter;

import li.cil.scannable.api.scanning.ScanFilterBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tags.ITag;

public final class ScanFilterBlockTag implements ScanFilterBlock {
    private final ITag<Block> tag;

    public ScanFilterBlockTag(final ITag<Block> tag) {
        this.tag = tag;
    }

    @Override
    public boolean matches(final BlockState state) {
        return tag.contains(state.getBlock());
    }
}
