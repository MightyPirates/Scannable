package li.cil.scannable.client.scanning.filter;

import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;

import java.util.function.Predicate;

@Environment(EnvType.CLIENT)
public record FluidTagScanFilter(Tag<Fluid> tag) implements Predicate<BlockState> {
    @Override
    public boolean test(final BlockState state) {
        final FluidState fluidState = state.getFluidState();
        return !fluidState.isEmpty() && tag.contains(fluidState.getType());
    }
}
