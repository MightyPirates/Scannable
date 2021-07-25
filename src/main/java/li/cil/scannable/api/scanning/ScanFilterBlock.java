package li.cil.scannable.api.scanning;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Filter provided by {@link ScannerModuleBlock} instances for the built-in block {@link ScanResultProvider}.
 * <p>
 * Filters are queried for each potential candidate and should therefore be as efficient as possible. For this
 * reason they are their own object (instead of directly querying the module), so that they may perform some
 * internal caching.
 */
@OnlyIn(Dist.CLIENT)
public interface ScanFilterBlock {
    /**
     * Queried for each potential block when performing a scan. Used to determine whether to include
     * the block in the scan result or not.
     *
     * @param state the block to check.
     * @return <code>true</code> to have the block included in the result; <code>false</code> otherwise.
     */
    boolean matches(final BlockState state);
}
