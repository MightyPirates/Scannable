package li.cil.scannable.api.scanning;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

/**
 * Interface for a scan result provider.
 * <p>
 * This is essentially just a fancy {@link Iterator} with support for partial
 * advancement so as to allow distributing scan workload over multiple ticks.
 * <p>
 * If your implementation is not that computationally expensive, it is fine to
 * just collect all scan results in {@link #collectScanResults(IBlockReader, Consumer)}.
 * <p>
 * Otherwise, the implementation should prepare for spread out collection of
 * results in {@link #initialize(PlayerEntity, Collection, Vector3d, float, int)},
 * over the specified number of ticks. Each tick until the scan is complete,
 * {@link #computeScanResults()} will be called. Implementations should gather
 * results and return all valid results when <code>collectScanResults</code>
 * is called.
 */
@OnlyIn(Dist.CLIENT)
public interface ScanResultProvider extends IForgeRegistryEntry<ScanResultProvider> {
    /**
     * Called each time a scan is started by the player.
     * <p>
     * Prepare internal structures for incoming calls to {@link #computeScanResults()}.
     * <p>
     * Note that the radius should be treated as a <em>maximum</em> radius.
     * Implementations are free to only scan smaller area if they so please.
     *
     * @param player    the player that is scanning.
     * @param modules   the modules installed in the scanner.
     * @param center    the center of the scanned sphere.
     * @param radius    the maximum radius of the scanned sphere.
     * @param scanTicks the total number of ticks the scan will take.
     */
    void initialize(final PlayerEntity player, final Collection<ItemStack> modules, final Vector3d center, final float radius, final int scanTicks);

    /**
     * Called each tick during an ongoing scan. Perform internal computations
     * to build a list of scan results over several ticks.
     */
    void computeScanResults();

    /**
     * Called after scanning is completed to collect the final results. Perform
     * any post-scan filtering and baking here and report results using the
     * provided callback.
     *
     * @param world    the world to check for.
     * @param callback the callback to feed results to.
     */
    void collectScanResults(final IBlockReader world, final Consumer<ScanResult> callback);

    /**
     * Render the specified results.
     * <p>
     * This is delegated as a batch call to the provider to allow optimized
     * rendering of large numbers of results. The provided results are
     * guaranteed to have been produced by this provider via its
     * {@link #collectScanResults(IBlockReader, Consumer)} method.
     * <p>
     * The specified list has been frustum culled using the results' bounds
     * provided from {@link ScanResult#getRenderBounds()}.
     *
     * @param renderTypeBuffer the buffer to use for batched rendering.
     * @param matrixStack      the matrix stack for rendering.
     * @param projectionMatrix the current projection matrix.
     * @param renderInfo       the active render info.
     * @param partialTicks     partial ticks of the currently rendered frame.
     * @param results          the results to render.
     */
    void render(final IRenderTypeBuffer renderTypeBuffer, final MatrixStack matrixStack, final Matrix4f projectionMatrix, final ActiveRenderInfo renderInfo, final float partialTicks, final List<ScanResult> results);

    /**
     * Called when a scan is complete or is canceled.
     * <p>
     * Use this to dispose internal structures to avoid memory leaks. This is
     * called after the scan is no longer being rendered, so this can be used
     * to clean up rendering data as well.
     */
    void reset();
}
