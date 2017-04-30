package li.cil.scannable.api.scanning;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;

import java.util.Collection;
import java.util.Iterator;

/**
 * Interface for a scan result provider.
 * <p>
 * This is essentially just a fancy {@link Iterator} with support for partial
 * advancement so as to allow distributing scan workload over multiple ticks.
 * <p>
 * If your implementation is not that computationally expensive, it is fine to
 * just collect all scan results in the first {@link #computeScanResults(Collection)}
 * call.
 * <p>
 * Otherwise, the implementation should prepare for spread out collection of
 * results in {@link #initialize(EntityPlayer, Vec3d, float, int)}, over the
 * specified number of ticks. Each tick until the scan is complete,
 * {@link #computeScanResults(Collection)} will be called, in which the
 * implementation should add results collected this tick to the passed
 * collection. It is the responsibility of the implementation to ensure that
 * all results have been added by the end of the last tick's call to
 * {@link #computeScanResults(Collection)}.
 */
public interface ScanResultProvider {
    /**
     * Called each time a scan is started by the player.
     * <p>
     * Prepare internal structures for incoming calls to {@link #computeScanResults(Collection)}.
     *
     * @param player    the player that is scanning.
     * @param center    the center of the scanned sphere.
     * @param radius    the radius of the scanned sphere.
     * @param scanTicks the total number of ticks the scan will take.
     */
    void initialize(final EntityPlayer player, final Vec3d center, final float radius, final int scanTicks);

    /**
     * Called each tick during an ongoing scan. Add any results collected this
     * tick to the passed list of results. Do <em>not</em> remove stuff from
     * the passed collection. Seriously!
     *
     * @param results the collection to add results to.
     */
    void computeScanResults(final Collection<ScanResult> results);

    /**
     * Called when a scan is complete or is canceled.
     * <p>
     * Use this to dispose internal structures to avoid memory leaks.
     */
    void reset();
}
