package li.cil.scannable.client.scanning.filter;

import li.cil.scannable.api.scanning.ScanFilterBlock;
import li.cil.scannable.common.config.Settings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraftforge.common.Tags;

public final class ScanFilterRareOreCatchAll implements ScanFilterBlock {
    public static final ScanFilterRareOreCatchAll INSTANCE = new ScanFilterRareOreCatchAll();

    @Override
    public boolean matches(final BlockState state) {
        final Block block = state.getBlock();
        return !Settings.shouldIgnore(block) &&
                Tags.Blocks.ORES.contains(block) &&
                !Settings.commonOreBlocks.contains(block) &&
                Settings.commonOreBlockTags.stream().noneMatch(t -> t.contains(block));
    }
}
