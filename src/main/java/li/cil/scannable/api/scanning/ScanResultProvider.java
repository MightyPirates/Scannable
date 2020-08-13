package li.cil.scannable.api.scanning;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
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
 * just collect all scan results in the first {@link #computeScanResults(Consumer)}
 * call.
 * <p>
 * Otherwise, the implementation should prepare for spread out collection of
 * results in {@link #initialize(PlayerEntity, Collection, Vec3d, float, int)},
 * over the specified number of ticks. Each tick until the scan is complete,
 * {@link #computeScanResults(Consumer)} will be called, in which the
 * implementation should add results collected this tick to the passed
 * collection. It is the responsibility of the implementation to ensure that
 * all results have been added by the end of the last tick's call to
 * {@link #computeScanResults(Consumer)}.
 */
@OnlyIn(Dist.CLIENT)
public interface ScanResultProvider extends IForgeRegistryEntry<ScanResultProvider> {
    /**
     * Called each time a scan is started by the player.
     * <p>
     * Prepare internal structures for incoming calls to {@link #computeScanResults(Consumer)}.
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
    void initialize(final PlayerEntity player, final Collection<ItemStack> modules, final Vec3d center, final float radius, final int scanTicks);

    /**
     * Called each tick during an ongoing scan. Report any results generated this
     * tick to the passed callback.
     *
     * @param callback the callback to feed results to.
     */
    void computeScanResults(final Consumer<ScanResult> callback);

    /**
     * Called to allow scan results to perform in-advance caching before rendering
     * commences and serves to filter out invalid results when moving results to
     * the active render list. Use this to filter out results that are no longer
     * valid, either because the scanned object has become invalid in the meantime,
     * or because the result has been merged into another one (e.g. the built-in
     * ore scanner merges adjacent ore blocks into a single result for better
     * render performance).
     *
     * @param world  the world to check for.
     * @param result the result to filter.
     * @return <code>true</code> if the result should be kept; <code>false</code> otherwise.
     */
    boolean bakeResult(final IBlockReader world, final ScanResult result);

    /**
     * Render the specified results.
     * <p>
     * This is delegated as a batch call to the provider to allow optimized
     * rendering of large numbers of results. The provided results are
     * guaranteed to have been produced by this provider via its
     * {@link #computeScanResults(Consumer)} method.
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
