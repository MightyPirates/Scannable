package li.cil.scannable.client.scanning.filter;

import li.cil.scannable.api.scanning.ScanFilterBlock;
import li.cil.scannable.common.scanning.ScannerModuleOreCommon;
import li.cil.scannable.common.scanning.filter.ScanFilterIgnoredBlocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;

public final class ScanFilterRareOreCatchAll implements ScanFilterBlock {
    @Override
    public boolean matches(final BlockState state) {
        return !ScanFilterIgnoredBlocks.shouldIgnore(state) &&
               Tags.Blocks.ORES.contains(state.getBlock()) &&
               !ScannerModuleOreCommon.matches(state);
    }
}
