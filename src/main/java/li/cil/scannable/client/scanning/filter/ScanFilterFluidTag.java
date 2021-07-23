package li.cil.scannable.client.scanning.filter;

import li.cil.scannable.api.scanning.ScanFilterBlock;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.tags.ITag;

public final class ScanFilterFluidTag implements ScanFilterBlock {
    private final ITag<Fluid> tag;

    public ScanFilterFluidTag(final ITag<Fluid> tag) {
        this.tag = tag;
    }

    @Override
    public boolean matches(final BlockState state) {
        final FluidState fluidState = state.getFluidState();
        return !fluidState.isEmpty() && tag.contains(fluidState.getType());
    }
}
