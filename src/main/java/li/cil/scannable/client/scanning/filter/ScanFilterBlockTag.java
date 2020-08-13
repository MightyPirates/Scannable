package li.cil.scannable.client.scanning.filter;

import li.cil.scannable.api.scanning.ScanFilterBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tags.Tag;

public final class ScanFilterBlockTag implements ScanFilterBlock {
    private final Tag<Block> tag;

    public ScanFilterBlockTag(final Tag<Block> tag) {
        this.tag = tag;
    }

    @Override
    public boolean matches(final BlockState state) {
        return tag.contains(state.getBlock());
    }
}
