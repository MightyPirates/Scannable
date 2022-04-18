package li.cil.scannable.client.scanning.filter;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Predicate;

@Environment(EnvType.CLIENT)
public record BlockTagScanFilter(TagKey<Block> tag) implements Predicate<BlockState> {
    @Override
    public boolean test(final BlockState state) {
        return state.is(tag);
    }
}
