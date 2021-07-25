package li.cil.scannable.client.scanning.filter;

import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

import java.util.function.Predicate;

public record FluidTagScanFilter(Tag<Fluid> tag) implements Predicate<BlockState> {
    @Override
    public boolean test(final BlockState state) {
        final FluidState fluidState = state.getFluidState();
        return !fluidState.isEmpty() && tag.contains(fluidState.getType());
    }
}
