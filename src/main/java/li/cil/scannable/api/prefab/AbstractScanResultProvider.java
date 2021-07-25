package li.cil.scannable.api.prefab;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import li.cil.scannable.api.scanning.ScanResultProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Helper base class for scan result providers, providing some common
 * functionality for drawing result information.
 */
@OnlyIn(Dist.CLIENT)
public abstract class AbstractScanResultProvider extends ForgeRegistryEntry<ScanResultProvider> implements ScanResultProvider {
    protected Player player;
    protected Vec3 center;
    protected float radius;

    // --------------------------------------------------------------------- //
    // ScanResultProvider

    @Override
    public void initialize(final Player player, final Collection<ItemStack> modules, final Vec3 center, final float radius, final int scanTicks) {
        this.player = player;
        this.center = center;
        this.radius = radius;
    }

    @Override
    public void reset() {
        player = null;
        center = null;
        radius = 0f;
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
    protected static void renderIconLabel(final MultiBufferSource bufferSource, final PoseStack poseStack, final float yaw, final float pitch, final Vec3 lookVec, final Vec3 viewerEyes, final float displayDistance, final Vec3 resultPos, final ResourceLocation icon, @Nullable final Component label) {
        final Vec3 toResult = resultPos.subtract(viewerEyes);
        final float distance = (float) toResult.length();
        final float lookDirDot = (float) lookVec.dot(toResult.normalize());
        final float sqLookDirDot = lookDirDot * lookDirDot;
        final float sq2LookDirDot = sqLookDirDot * sqLookDirDot;
        final float focusScale = Mth.clamp(sq2LookDirDot * sq2LookDirDot + 0.005f, 0.5f, 1f);
        final float scale = distance * focusScale * 0.005f;

        poseStack.pushPose();
        poseStack.translate(resultPos.x, resultPos.y, resultPos.z);
        poseStack.mulPose(Vector3f.YN.rotationDegrees(yaw));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(pitch));
        poseStack.scale(-scale, -scale, scale);

        if (lookDirDot > 0.999f && label != null) {
            final Component text;
            if (displayDistance > 0) {
                text = li.cil.scannable.common.config.Strings.withDistance(label, Mth.ceil(displayDistance));
            } else {
                text = label;
            }

            final Font font = Minecraft.getInstance().font;
            final int width = font.width(text) + 16;

            poseStack.pushPose();
            poseStack.translate(width / 2f, 0, 0);

            drawQuad(bufferSource.getBuffer(getRenderLayer()), poseStack, width, font.lineHeight + 5, 0, 0, 0, 0.6f);

            poseStack.popPose();
            font.drawInBatch(text, 12, -4, 0xFFFFFFFF, true, poseStack.last().pose(), bufferSource, true, 0, 0xf000f0);
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
        final Matrix4f matrix = poseStack.last().pose();
        buffer.vertex(matrix, -width * 0.5f, height * 0.5f, 0).color(r, g, b, a).uv(0, 1f).endVertex();
        buffer.vertex(matrix, width * 0.5f, height * 0.5f, 0).color(r, g, b, a).uv(1f, 1f).endVertex();
        buffer.vertex(matrix, width * 0.5f, -height * 0.5f, 0).color(r, g, b, a).uv(1f, 0).endVertex();
        buffer.vertex(matrix, -width * 0.5f, -height * 0.5f, 0).color(r, g, b, a).uv(0, 0).endVertex();
    }

    // --------------------------------------------------------------------- //
    // Simple render layers for result rendering.

    protected static RenderType getRenderLayer() {
        return RenderType.create("scan_result",
                DefaultVertexFormat.POSITION_COLOR,
                VertexFormat.Mode.QUADS, 65536,
                false,
                false,
                RenderType.CompositeState.builder()
                        .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getPositionColorShader))
                        .setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
                        .setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
                        .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                        .createCompositeState(false));
    }

    protected static RenderType getRenderLayer(final ResourceLocation textureLocation) {
        return RenderType.create("scan_result",
                DefaultVertexFormat.POSITION_TEX,
                VertexFormat.Mode.QUADS, 65536,
                false,
                false,
                RenderType.CompositeState.builder()
                        .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getPositionTexShader))
                        .setTextureState(new RenderStateShard.TextureStateShard(textureLocation, false, false))
                        .setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
                        .setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
                        .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                        .createCompositeState(false));
    }
}
