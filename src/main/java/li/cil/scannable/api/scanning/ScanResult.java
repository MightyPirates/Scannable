package li.cil.scannable.api.scanning;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;

/**
 * Represents a single logical scan result, for which one single visualization
 * is rendered.
 */
public interface ScanResult {
    /**
     * Get the in-world location of this scan result.
     * <p>
     * This is used by the scan result manager to sort results such that they
     * appear after another with increasing distance to the scan origin.
     *
     * @return the in-world position of the scan result.
     */
    Vec3d getPosition();

    /**
     * A bounding box encompassing anything the result may render.
     * <p>
     * May return <code>null</code> to ignore frustum culling.
     *
     * @return the render bounding box for the result.
     */
    @Nullable
    AxisAlignedBB getRenderBounds();
}
