package li.cil.scannable.client.scanning.filter;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

import java.util.function.Predicate;

@Environment(EnvType.CLIENT)
public record FluidTagScanFilter(TagKey<Fluid> tag) implements Predicate<BlockState> {
    @Override
    public boolean test(final BlockState state) {
        final FluidState fluidState = state.getFluidState();
        return !fluidState.isEmpty() && fluidState.is(tag);
    }
}
