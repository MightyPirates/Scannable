package li.cil.scannable.api.scanning;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

/**
 * Interface for a scan result provider.
 * <p>
 * Provide this as a capability in an item so that it can be installed in the
 * scanner. Once installed, it will be queried when the scanner is used.
 * <p>
 * This is essentially just a fancy {@link Iterator} with support for partial
 * advancement so as to allow distributing scan workload over multiple ticks.
 * <p>
 * If your implementation is not that computationally expensive, it is fine to
 * just collect all scan results in the first {@link #computeScanResults(Consumer)}
 * call.
 * <p>
 * Otherwise, the implementation should prepare for spread out collection of
 * results in {@link #initialize(EntityPlayer, Collection, Vec3d, float, int)},
 * over the specified number of ticks. Each tick until the scan is complete,
 * {@link #computeScanResults(Consumer)} will be called, in which the
 * implementation should add results collected this tick to the passed
 * collection. It is the responsibility of the implementation to ensure that
 * all results have been added by the end of the last tick's call to
 * {@link #computeScanResults(Consumer)}.
 * <p>
 * Note that all of the scanning behavior is <em>client side only</em>, none
 * of these methods will ever be called on the server. However, the capability
 * must also be <em>reported</em> as present on the server so that both sides
 * can detect if there are any scan providers installed and ignore the scan if
 * there are not. The capability does not in fact have to be gettable, the
 * server only does a <code>hasCapability</code> check.
 */
public interface ScanResultProvider {
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
    void initialize(final EntityPlayer player, final Collection<ItemStack> modules, final Vec3d center, final float radius, final int scanTicks);

    /**
     * Called each tick during an ongoing scan. Add any results collected this
     * tick to the passed list of results. Do <em>not</em> remove stuff from
     * the passed collection. Seriously!
     *
     * @param callback the callback to feed results to.
     */
    void computeScanResults(final Consumer<ScanResult> callback);

    /**
     * Called to filter out invalid results when moving results to the active
     * render list. Use this to filter out results that are no longer valid,
     * either because the scanned object has become invalid in the meantime,
     * or because the result has been merged into another one (e.g. the built-in
     * ore scanner merges adjacent ore blocks into a single result for better
     * render performance).
     *
     * @param result the result to filter.
     * @return <code>true</code> if the result should be kept; <code>false</code> otherwise.
     */
    boolean isValid(final ScanResult result);

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
     * @param entity       the entity we're rendering for. Usually the player.
     * @param results      the results to render.
     * @param partialTicks partial ticks of the currently rendered frame.
     */
    void render(final Entity entity, final List<ScanResult> results, final float partialTicks);

    /**
     * Called when a scan is complete or is canceled.
     * <p>
     * Use this to dispose internal structures to avoid memory leaks. This is
     * called after the scan is no longer being rendered, so this can be used
     * to clean up rendering data as well.
     */
    void reset();
}
