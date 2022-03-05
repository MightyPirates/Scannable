package li.cil.scannable.client.scanning.filter;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;

import java.util.function.Predicate;

@Environment(EnvType.CLIENT)
public record BlockScanFilter(Block block) implements Predicate<BlockState> {
    @Override
    public boolean test(final BlockState state) {
        return block == state.getBlock();
    }
}
