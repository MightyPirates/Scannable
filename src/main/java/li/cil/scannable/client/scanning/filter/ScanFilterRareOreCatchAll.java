package li.cil.scannable.client.scanning.filter;

import li.cil.scannable.api.scanning.ScanFilterBlock;
import li.cil.scannable.common.scanning.ScannerModuleOreCommon;
import net.minecraft.block.BlockState;
import net.minecraftforge.common.Tags;

public final class ScanFilterRareOreCatchAll implements ScanFilterBlock {
    public static final ScanFilterRareOreCatchAll INSTANCE = new ScanFilterRareOreCatchAll();

    @Override
    public boolean matches(final BlockState state) {
        return !ScanFilterUtils.shouldIgnore(state) &&
               Tags.Blocks.ORES.contains(state.getBlock()) &&
               !ScannerModuleOreCommon.matches(state);
    }
}
