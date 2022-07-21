package li.cil.scannable.api.scanning;

import com.mojang.blaze3d.vertex.PoseStack;
import li.cil.scannable.api.API;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;
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
 * just collect all scan results in {@link #collectScanResults(BlockGetter, Consumer)}.
 * <p>
 * Otherwise, the implementation should prepare for spread out collection of
 * results in {@link #initialize(Player, Collection, Vec3, float, int)},
 * over the specified number of ticks. Each tick until the scan is complete,
 * {@link #computeScanResults()} will be called. Implementations should gather
 * results and return all valid results when <code>collectScanResults</code>
 * is called.
 */
@OnlyIn(Dist.CLIENT)
public interface ScanResultProvider extends IForgeRegistryEntry<ScanResultProvider> {
    /**
     * The registry name of the registry holding scan result providers.
     */
    ResourceKey<Registry<ScanResultProvider>> REGISTRY = ResourceKey.createRegistryKey(new ResourceLocation(API.MOD_ID, "scan_result_provider"));

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
    void initialize(final Player player, final Collection<ItemStack> modules, final Vec3 center, final float radius, final int scanTicks);

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
     * @param level    the level to check for.
     * @param callback the callback to feed results to.
     */
    void collectScanResults(final BlockGetter level, final Consumer<ScanResult> callback);

    /**
     * Render the specified results.
     * <p>
     * This is delegated as a batch call to the provider to allow optimized
     * rendering of large numbers of results. The provided results are
     * guaranteed to have been produced by this provider via its
     * {@link #collectScanResults(BlockGetter, Consumer)} method.
     * <p>
     * The specified list has been frustum culled using the results' bounds
     * provided from {@link ScanResult#getRenderBounds()}.
     *
     * @param bufferSource the buffer source to use for batched rendering.
     * @param poseStack    the pose stack for rendering.
     * @param renderInfo   the active render info.
     * @param partialTicks partial ticks of the currently rendered frame.
     * @param results      the results to render.
     * @deprecated Use the version taking the current render context instead.
     */
    @Deprecated
    default void render(final MultiBufferSource bufferSource, final PoseStack poseStack, final Camera renderInfo, final float partialTicks, final List<ScanResult> results) {
        render(ScanResultRenderContext.WORLD, bufferSource, poseStack, renderInfo, partialTicks, results);
    }

    /**
     * Render the specified results.
     * <p>
     * This is delegated as a batch call to the provider to allow optimized
     * rendering of large numbers of results. The provided results are
     * guaranteed to have been produced by this provider via its
     * {@link #collectScanResults(BlockGetter, Consumer)} method.
     * <p>
     * The specified list has been frustum culled using the results' bounds
     * provided from {@link ScanResult#getRenderBounds()}.
     *
     * @param context      the current rendering context.
     * @param bufferSource the buffer source to use for batched rendering.
     * @param poseStack    the pose stack for rendering.
     * @param renderInfo   the active render info.
     * @param partialTicks partial ticks of the currently rendered frame.
     * @param results      the results to render.
     */
    default void render(final ScanResultRenderContext context, final MultiBufferSource bufferSource, final PoseStack poseStack, final Camera renderInfo, final float partialTicks, final List<ScanResult> results) {
        render(bufferSource, poseStack, renderInfo, partialTicks, results);
    }

    /**
     * Called when a scan is complete or is canceled.
     * <p>
     * Use this to dispose internal structures to avoid memory leaks. This is
     * called after the scan is no longer being rendered, so this can be used
     * to clean up rendering data as well.
     */
    void reset();
}
