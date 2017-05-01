package li.cil.scannable.api.scanning;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec2f;
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
     * Called before the first render call to this scan result.
     * <p>
     * Use this to set up additional data that gets cleaned up in {@link #dispose()}.
     */
    void initialize();

    /**
     * Called when results are discarded, either because their lifetime expired
     * or because a new scan was issued.
     * <p>
     * Only called if {@link #initialize()} was called on the result, i.e. in
     * particular this is not called if the scan yielding this result was never
     * finished.
     */
    void dispose();

    /**
     * A bounding box encompassing anything the result may render.
     * <p>
     * May return <code>null</code> to ignore frustum culling.
     *
     * @return the render bounding box for the result.
     */
    @Nullable
    AxisAlignedBB getRenderBounds();

    /**
     * Called when the result should render itself in the world.
     * <p>
     * This is purely for convenience, so implementors don't have to also
     * register to the corresponding event.
     *
     * @param player       the entity we're rendering for. Usually the player.
     * @param playerPos    the interpolated position of the entity.
     * @param playerAngle  the interpolated entity yaw and pitch.
     * @param partialTicks partial ticks of the currently rendered frame.
     */
    void render(final Entity player, final Vec3d playerPos, final Vec2f playerAngle, final float partialTicks);
}
