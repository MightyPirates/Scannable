package li.cil.scannable.client.scanning.filter;

import li.cil.scannable.api.scanning.ScanFilterBlock;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public record ScanFilterBlockTag(Tag<Block> tag) implements ScanFilterBlock {
    @Override
    public boolean matches(final BlockState state) {
        return tag.contains(state.getBlock());
    }
}
