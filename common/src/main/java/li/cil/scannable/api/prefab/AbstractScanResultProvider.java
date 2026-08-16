package li.cil.scannable.api.prefab;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import li.cil.scannable.api.scanning.ScanResultProvider;
import li.cil.scannable.client.shader.ScanPipelines;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static li.cil.scannable.util.UnitConversion.toRadians;

/**
 * Helper base class for scan result providers, providing some common
 * functionality for drawing result information.
 */
@Environment(EnvType.CLIENT)
public abstract class AbstractScanResultProvider implements ScanResultProvider {
    private static final RenderType OVERLAY_LAYER = RenderType.create("scan_result_overlay",
        RenderSetup.builder(ScanPipelines.SCAN_RESULT_OVERLAY).createRenderSetup());
    private static final Map<Identifier, RenderType> TEXTURED_OVERLAY_LAYERS = new ConcurrentHashMap<>();

    protected Player player;
    protected Vec3 center;
    protected int radius;

    // --------------------------------------------------------------------- //
    // ScanResultProvider

    @Override
    public void initialize(final Player player, final Collection<ItemStack> modules, final Vec3 center, final float radius, final int scanTicks) {
        this.player = player;
        this.center = center;
        this.radius = (int)radius;
    }

    @Override
    public void reset() {
        player = null;
        center = null;
        radius = 0;
    }

    // --------------------------------------------------------------------- //

    /**
     * Renders an icon with a label that is only shown when looked at. This is
     * what's used to render the entity labels for example.
     *
     * @param bufferSource    the buffer source to use for batch rendering.
     * @param poseStack       the pose stack for rendering.
     * @param yaw             the interpolated yaw of the viewer.
     * @param pitch           the interpolated pitch of the viewer.
     * @param lookVec         the look vector of the viewer.
     * @param viewerEyes      the eye position of the viewer.
     * @param displayDistance the distance to show in the label. Zero or negative to hide.
     * @param resultPos       the interpolated position of the result.
     * @param icon            the icon to display.
     * @param label           the label text. May be null.
     */
    protected static void renderIconLabel(final MultiBufferSource bufferSource, final PoseStack poseStack, final float yaw, final float pitch, final Vec3 lookVec, final Vec3 viewerEyes, final float displayDistance, final Vec3 resultPos, final Identifier icon, @Nullable final Component label) {
        final Vec3 toResult = resultPos.subtract(viewerEyes);
        final float distance = (float) toResult.length();
        final float lookDirDot = (float) lookVec.dot(toResult.normalize());
        final float sqLookDirDot = lookDirDot * lookDirDot;
        final float sq2LookDirDot = sqLookDirDot * sqLookDirDot;
        final float focusScale = Mth.clamp(sq2LookDirDot * sq2LookDirDot + 0.005f, 0.5f, 1f);
        final float scale = distance * focusScale * 0.005f;

        poseStack.pushPose();
        poseStack.translate(resultPos.x, resultPos.y, resultPos.z);
        poseStack.mulPose(new Quaternionf().rotationY(toRadians(-yaw)));
        poseStack.mulPose(new Quaternionf().rotationX(toRadians(pitch)));
        poseStack.scale(-scale, -scale, scale);

        if (lookDirDot > 0.999f && label != null) {
            final Component text;
            if (displayDistance > 0) {
                text = withDistance(label, Mth.ceil(displayDistance));
            } else {
                text = label;
            }

            final Font font = Minecraft.getInstance().font;
            final int width = font.width(text) + 16;

            poseStack.pushPose();
            poseStack.translate(width / 2f, 0, 0);

            drawQuad(bufferSource.getBuffer(getRenderLayer()), poseStack, width, font.lineHeight + 5, 0, 0, 0, 0.6f);

            poseStack.popPose();
            font.drawInBatch(text, 12, -4, 0xFFFFFFFF, true, poseStack.last().pose(), bufferSource, Font.DisplayMode.SEE_THROUGH, 0, 0xf000f0);
            // HACK This is a workaround for text shadow rendering on top of main text in 1.20.  Can't figure out why.
            font.drawInBatch(text, 12, -4, 0xFFFFFFFF, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.SEE_THROUGH, 0, 0xf000f0);
        }

        drawQuad(bufferSource.getBuffer(getRenderLayer(icon)), poseStack, 16, 16);

        poseStack.popPose();
    }

    // --------------------------------------------------------------------- //
    // Drawing simple primitives in an existing buffer.

    protected static void drawQuad(final VertexConsumer buffer, final PoseStack poseStack, final float width, final float height) {
        drawQuad(buffer, poseStack, width, height, 1, 1, 1, 1);
    }

    protected static void drawQuad(final VertexConsumer buffer, final PoseStack poseStack, final float width, final float height, final float r, final float g, final float b, final float a) {
        final var matrix = poseStack.last().pose();
        buffer.addVertex(matrix, -width * 0.5f, height * 0.5f, 0).setColor(r, g, b, a).setUv(0, 1f);
        buffer.addVertex(matrix, width * 0.5f, height * 0.5f, 0).setColor(r, g, b, a).setUv(1f, 1f);
        buffer.addVertex(matrix, width * 0.5f, -height * 0.5f, 0).setColor(r, g, b, a).setUv(1f, 0);
        buffer.addVertex(matrix, -width * 0.5f, -height * 0.5f, 0).setColor(r, g, b, a).setUv(0, 0);
    }

    // --------------------------------------------------------------------- //
    // Simple render layers for result rendering.

    protected static RenderType getRenderLayer() {
        return OVERLAY_LAYER;
    }

    protected static RenderType getRenderLayer(final Identifier textureLocation) {
        return TEXTURED_OVERLAY_LAYERS.computeIfAbsent(textureLocation, location ->
            RenderType.create("scan_result_overlay_textured",
                RenderSetup.builder(ScanPipelines.SCAN_RESULT_OVERLAY_TEXTURED)
                    .withTexture("Sampler0", location)
                    .createRenderSetup()));
    }

    // --------------------------------------------------------------------- //

    private static Component withDistance(final Component caption, final float distance) {
        return Component.translatable("gui.scannable.overlay.distance", caption, Mth.ceil(distance));
    }
}
