package li.cil.scannable.client.scanning.filter;

import li.cil.scannable.api.scanning.ScanFilterBlock;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tags.Tag;

public final class ScanFilterFluidTag implements ScanFilterBlock {
    private final Tag<Fluid> tag;

    public ScanFilterFluidTag(final Tag<Fluid> tag) {
        this.tag = tag;
    }

    @Override
    public boolean matches(final BlockState state) {
        final IFluidState fluidState = state.getFluidState();
        return !fluidState.isEmpty() && tag.contains(fluidState.getFluid());
    }
}
