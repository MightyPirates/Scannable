package li.cil.scannable.api.scanning;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.io.Closeable;

/**
 * Represents a single logical scan result, for which one single visualization
 * is rendered using the provider that produced this result.
 */
public interface ScanResult extends Closeable {
    /**
     * Get the in-world location of this scan result.
     * <p>
     * This is used by the scan result manager to sort results such that they
     * appear after another with increasing distance to the scan origin.
     *
     * @return the in-world position of the scan result.
     */
    Vec3 getPosition();

    /**
     * A bounding box encompassing anything the result may render.
     * <p>
     * May return <code>null</code> to ignore frustum culling.
     *
     * @return the render bounding box for the result.
     */
    @Nullable
    AABB getRenderBounds();

    /**
     * Called when results are disposed to allow freeing non-managed resources
     * potentially used for rendering (e.g. VBOs).
     */
    @Override
    default void close() {
    }
}
