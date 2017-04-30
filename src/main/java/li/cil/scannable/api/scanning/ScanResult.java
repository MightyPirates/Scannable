package li.cil.scannable.api.scanning;

import net.minecraft.util.math.Vec3d;

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
     * The render type of the scan result.
     * <p>
     * This is used to determine into which render list the result will be put.
     * Note that changing the type later on is not possible. It is only read
     * once after the scan result was retrieved from its {@link ScanResultProvider}.
     *
     * @return the render type of this result.
     */
    ScanResultRenderType getRenderType();

    /**
     * Callback when rendering using {@link ScanResultRenderType#DIEGETIC} mode.
     * <p>
     * This is purely for convenience, so implementors don't have to also
     * register to the corresponding event.
     *
     * @param partialTicks partial ticks of the currently rendered frame.
     */
    void renderDiegetic(final float partialTicks);

    /**
     * Callback when rendering using {@link ScanResultRenderType#NON_DIEGETIC} mode.
     * <p>
     * This is purely for convenience, so implementors don't have to also
     * register to the corresponding event.
     *
     * @param partialTicks partial ticks of the currently rendered frame.
     */
    void renderNonDiegetic(final float partialTicks);
}
