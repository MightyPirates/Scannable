package li.cil.scannable.api.prefab;

import com.google.common.base.Strings;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import li.cil.scannable.api.scanning.ScanResultProvider;
import li.cil.scannable.common.config.Constants;
import li.cil.scannable.util.Migration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Helper base class for scan result providers, providing some common
 * functionality for drawing result information.
 */
@OnlyIn(Dist.CLIENT)
public abstract class AbstractScanResultProvider extends ForgeRegistryEntry<ScanResultProvider> implements ScanResultProvider {
    protected PlayerEntity player;
    protected Vector3d center;
    protected float radius;

    // --------------------------------------------------------------------- //
    // ScanResultProvider

    @Override
    public void initialize(final PlayerEntity player, final Collection<ItemStack> modules, final Vector3d center, final float radius, final int scanTicks) {
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
     * @param renderTypeBuffer the buffer to user for batch rendering.
     * @param matrixStack      the matrix stack for rendering.
     * @param yaw              the interpolated yaw of the viewer.
     * @param pitch            the interpolated pitch of the viewer.
     * @param lookVec          the look vector of the viewer.
     * @param viewerEyes       the eye position of the viewer.
     * @param displayDistance  the distance to show in the label. Zero or negative to hide.
     * @param resultPos        the interpolated position of the result.
     * @param icon             the icon to display.
     * @param label            the label text. May be null.
     */
    protected static void renderIconLabel(final IRenderTypeBuffer renderTypeBuffer, final MatrixStack matrixStack, final float yaw, final float pitch, final Vector3d lookVec, final Vector3d viewerEyes, final float displayDistance, final Vector3d resultPos, final ResourceLocation icon, @Nullable final ITextComponent label) {
        final Vector3d toResult = resultPos.subtract(viewerEyes);
        final float distance = (float) toResult.length();
        final float lookDirDot = (float) lookVec.dotProduct(toResult.normalize());
        final float sqLookDirDot = lookDirDot * lookDirDot;
        final float sq2LookDirDot = sqLookDirDot * sqLookDirDot;
        final float focusScale = MathHelper.clamp(sq2LookDirDot * sq2LookDirDot + 0.005f, 0.5f, 1f);
        final float scale = distance * focusScale * 0.005f;

        matrixStack.push();
        matrixStack.translate(resultPos.x, resultPos.y, resultPos.z);
        matrixStack.rotate(Vector3f.YN.rotationDegrees(yaw));
        matrixStack.rotate(Vector3f.XP.rotationDegrees(pitch));
        matrixStack.scale(-scale, -scale, scale);

        if (lookDirDot > 0.999f && label != null && !Strings.isNullOrEmpty(label.getString())) {
            final ITextComponent text;
            if (displayDistance > 0) {
                text = new TranslationTextComponent(Constants.GUI_OVERLAY_LABEL_DISTANCE, label, MathHelper.ceil(displayDistance));
            } else {
                text = label;
            }

            final FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
            final int width = Migration.FontRenderer.getStringWidth(fontRenderer, text) + 16;

            matrixStack.push();
            matrixStack.translate(width / 2f, 0, 0);

            drawQuad(renderTypeBuffer.getBuffer(getRenderLayer()), matrixStack, width, fontRenderer.FONT_HEIGHT + 5, 0, 0, 0, 0.6f);

            matrixStack.pop();
            Migration.FontRenderer.renderString(fontRenderer, text, 12, -4, 0xFFFFFFFF, true, matrixStack.getLast().getMatrix(), renderTypeBuffer, true, 0, 0xf000f0);
        }

        drawQuad(renderTypeBuffer.getBuffer(getRenderLayer(icon)), matrixStack, 16, 16);

        matrixStack.pop();
    }

    // --------------------------------------------------------------------- //
    // Drawing simple primitives in an existing buffer.

    protected static void drawQuad(final IVertexBuilder buffer, final MatrixStack matrixStack, final float width, final float height) {
        drawQuad(buffer, matrixStack, width, height, 1, 1, 1, 1);
    }

    protected static void drawQuad(final IVertexBuilder buffer, final MatrixStack matrixStack, final float width, final float height, final float r, final float g, final float b, final float a) {
        final Matrix4f matrix = matrixStack.getLast().getMatrix();
        buffer.pos(matrix, -width / 2, height / 2, 0).color(r, g, b, a).tex(0, 1).endVertex();
        buffer.pos(matrix, width / 2, height / 2, 0).color(r, g, b, a).tex(1, 1).endVertex();
        buffer.pos(matrix, width / 2, -height / 2, 0).color(r, g, b, a).tex(1, 0).endVertex();
        buffer.pos(matrix, -width / 2, -height / 2, 0).color(r, g, b, a).tex(0, 0).endVertex();
    }

    protected static void drawCube(final IVertexBuilder buffer, final Matrix4f matrix, final float minX, final float minY, final float minZ, final float maxX, final float maxY, final float maxZ, final float r, final float g, final float b, final float a) {
        drawPlaneNegX(buffer, matrix, minX, minY, maxY, minZ, maxZ, r, g, b, a * 0.9f);
        drawPlanePosX(buffer, matrix, maxX, minY, maxY, minZ, maxZ, r, g, b, a * 0.9f);
        drawPlaneNegY(buffer, matrix, minY, minX, maxX, minZ, maxZ, r, g, b, a * 0.8f);
        drawPlanePosY(buffer, matrix, maxY, minX, maxX, minZ, maxZ, r, g, b, a * 1.1f);
        drawPlaneNegZ(buffer, matrix, minZ, minX, maxX, minY, maxY, r, g, b, a);
        drawPlanePosZ(buffer, matrix, maxZ, minX, maxX, minY, maxY, r, g, b, a);
    }

    protected static void drawPlaneNegX(final IVertexBuilder buffer, final Matrix4f matrix, final float x, final float minY, final float maxY, final float minZ, final float maxZ, final float r, final float g, final float b, final float a) {
        buffer.pos(matrix, x, minY, minZ).color(r, g, b, a).tex(0, 1).endVertex();
        buffer.pos(matrix, x, minY, maxZ).color(r, g, b, a).tex(1, 1).endVertex();
        buffer.pos(matrix, x, maxY, maxZ).color(r, g, b, a).tex(1, 0).endVertex();
        buffer.pos(matrix, x, maxY, minZ).color(r, g, b, a).tex(0, 0).endVertex();
    }

    protected static void drawPlanePosX(final IVertexBuilder buffer, final Matrix4f matrix, final float x, final float minY, final float maxY, final float minZ, final float maxZ, final float r, final float g, final float b, final float a) {
        buffer.pos(matrix, x, minY, minZ).color(r, g, b, a).tex(0, 1).endVertex();
        buffer.pos(matrix, x, maxY, minZ).color(r, g, b, a).tex(1, 1).endVertex();
        buffer.pos(matrix, x, maxY, maxZ).color(r, g, b, a).tex(1, 0).endVertex();
        buffer.pos(matrix, x, minY, maxZ).color(r, g, b, a).tex(0, 0).endVertex();
    }

    protected static void drawPlaneNegY(final IVertexBuilder buffer, final Matrix4f matrix, final float y, final float minX, final float maxX, final float minZ, final float maxZ, final float r, final float g, final float b, final float a) {
        buffer.pos(matrix, minX, y, minZ).color(r, g, b, a).tex(0, 1).endVertex();
        buffer.pos(matrix, maxX, y, minZ).color(r, g, b, a).tex(1, 1).endVertex();
        buffer.pos(matrix, maxX, y, maxZ).color(r, g, b, a).tex(1, 0).endVertex();
        buffer.pos(matrix, minX, y, maxZ).color(r, g, b, a).tex(0, 0).endVertex();
    }

    protected static void drawPlanePosY(final IVertexBuilder buffer, final Matrix4f matrix, final float y, final float minX, final float maxX, final float minZ, final float maxZ, final float r, final float g, final float b, final float a) {
        buffer.pos(matrix, minX, y, minZ).color(r, g, b, a).tex(0, 1).endVertex();
        buffer.pos(matrix, minX, y, maxZ).color(r, g, b, a).tex(1, 1).endVertex();
        buffer.pos(matrix, maxX, y, maxZ).color(r, g, b, a).tex(1, 0).endVertex();
        buffer.pos(matrix, maxX, y, minZ).color(r, g, b, a).tex(0, 0).endVertex();
    }

    protected static void drawPlaneNegZ(final IVertexBuilder buffer, final Matrix4f matrix, final float z, final float minX, final float maxX, final float minY, final float maxY, final float r, final float g, final float b, final float a) {
        buffer.pos(matrix, minX, minY, z).color(r, g, b, a).tex(0, 1).endVertex();
        buffer.pos(matrix, minX, maxY, z).color(r, g, b, a).tex(1, 1).endVertex();
        buffer.pos(matrix, maxX, maxY, z).color(r, g, b, a).tex(1, 0).endVertex();
        buffer.pos(matrix, maxX, minY, z).color(r, g, b, a).tex(0, 0).endVertex();
    }

    protected static void drawPlanePosZ(final IVertexBuilder buffer, final Matrix4f matrix, final float z, final float minX, final float maxX, final float minY, final float maxY, final float r, final float g, final float b, final float a) {
        buffer.pos(matrix, minX, minY, z).color(r, g, b, a).tex(0, 1).endVertex();
        buffer.pos(matrix, maxX, minY, z).color(r, g, b, a).tex(1, 1).endVertex();
        buffer.pos(matrix, maxX, maxY, z).color(r, g, b, a).tex(1, 0).endVertex();
        buffer.pos(matrix, minX, maxY, z).color(r, g, b, a).tex(0, 0).endVertex();
    }

    // --------------------------------------------------------------------- //
    // Simple render layers for result rendering.

    protected static RenderType getRenderLayer() {
        return RenderType.makeType("scan_result",
                DefaultVertexFormats.POSITION_COLOR_TEX,
                GL11.GL_QUADS,
                65536,
                RenderType.State.getBuilder()
                        .transparency(RenderType.TRANSLUCENT_TRANSPARENCY)
                        .depthTest(RenderState.DEPTH_ALWAYS)
                        .writeMask(RenderState.COLOR_WRITE)
                        .build(false));
    }

    protected static RenderType getRenderLayer(final ResourceLocation textureLocation) {
        return RenderType.makeType("scan_result",
                DefaultVertexFormats.POSITION_COLOR_TEX,
                GL11.GL_QUADS,
                65536,
                RenderType.State.getBuilder()
                        .texture(new RenderState.TextureState(textureLocation, false, false))
                        .transparency(RenderType.TRANSLUCENT_TRANSPARENCY)
                        .depthTest(RenderState.DEPTH_ALWAYS)
                        .writeMask(RenderState.COLOR_WRITE)
                        .build(false));
    }
}
