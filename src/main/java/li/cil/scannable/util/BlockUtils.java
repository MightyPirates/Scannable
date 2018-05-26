package li.cil.scannable.util;

import li.cil.scannable.common.Scannable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class BlockUtils {
    @SuppressWarnings("deprecation")
    @Nullable
    public static ItemStack getItemStackFromState(final IBlockState state, @Nullable final World world) {
        final Block block = state.getBlock();
        try {
            return block.getPickBlock(state, null, world, BlockPos.ORIGIN, null);
        } catch (final Throwable t) {
            try {
                final Item item = Item.getItemFromBlock(block);
                final int damage = block.damageDropped(state);
                final ItemStack stack = new ItemStack(item, 1, damage);
                final int meta = item.getMetadata(stack);
                if (Objects.equals(block.getStateFromMeta(meta), state)) {
                    return stack;
                } else {
                    throw new Exception("Block/Item implementation does not allow round-trip via Block.damageDropped/Item.getMetadata/Block.getStateFromMeta: " + block.toString() + ", " + item.toString());
                }
            } catch (final Throwable t2) {
                if (loggedWarningFor.add(state)) {
                    // Log twice to get both stack traces. Don't log first trace if second lookup succeeds.
                    Scannable.getLog().debug("Failed determining dropped block for " + state.toString() + " via getPickBlock, trying to resolve via meta.", t);
                    Scannable.getLog().debug("Failed determining dropped block for " + state.toString() + " via meta.", t2);
                }
            }
        }
        return null;
    }

    // --------------------------------------------------------------------- //

    private static final Set<IBlockState> loggedWarningFor = Collections.synchronizedSet(new HashSet<>());

    // --------------------------------------------------------------------- //

    private BlockUtils() {
    }
}
