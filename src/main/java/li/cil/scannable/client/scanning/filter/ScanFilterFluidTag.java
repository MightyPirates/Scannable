package li.cil.scannable.client.scanning.filter;

import li.cil.scannable.api.scanning.ScanFilterBlock;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

public record ScanFilterFluidTag(Tag<Fluid> tag) implements ScanFilterBlock {
    @Override
    public boolean matches(final BlockState state) {
        final FluidState fluidState = state.getFluidState();
        return !fluidState.isEmpty() && tag.contains(fluidState.getType());
    }
}
