package li.cil.scannable.client.scanning.filter;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Predicate;

@OnlyIn(Dist.CLIENT)
public record BlockTagScanFilter(TagKey<Block> tag) implements Predicate<BlockState> {
    @Override
    public boolean test(final BlockState state) {
        return state.is(tag);
    }
}
