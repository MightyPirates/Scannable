package li.cil.scannable.api.prefab;

import li.cil.scannable.api.scanning.ScanResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;

/**
 * Helper base class for scan results, providing some common functionality for
 * drawing result information.
 */
public abstract class AbstractScanResult implements ScanResult {
    // --------------------------------------------------------------------- //
    // ScanResult

    @Override
    public void initialize() {
    }

    @Override
    public void dispose() {
    }

    @Nullable
    @Override
    public AxisAlignedBB getRenderBounds() {
        final Vec3d worldPos = getPosition();
        return new AxisAlignedBB(new BlockPos(worldPos));
    }

    @Override
    public void render(final Entity player, final Vec3d playerPos, final Vec2f playerAngle, final float partialTicks) {
        // By default we set up the render state for simple 2D overlays. If
        // you need to render something 3D either override this method or just
        // directly implement ScanResult.
        final Vec3d worldPos = getPosition(partialTicks);
        final float scale = (float) player.getPositionEyes(partialTicks).distanceTo(worldPos) * 0.005f;

        GlStateManager.pushMatrix();
        GlStateManager.translate(worldPos.xCoord - playerPos.xCoord, worldPos.yCoord - playerPos.yCoord, worldPos.zCoord - playerPos.zCoord);
        GlStateManager.rotate(-playerAngle.x, 0, 1, 0);
        GlStateManager.rotate(playerAngle.y, 1, 0, 0);
        GlStateManager.scale(-scale, -scale, scale);

        GlStateManager.disableLighting();
        GlStateManager.disableDepth();

        render2D(player, playerPos, playerAngle, partialTicks);

        GlStateManager.enableDepth();
        GlStateManager.enableLighting();

        GlStateManager.popMatrix();
    }

    // --------------------------------------------------------------------- //

    /**
     * Override this to provide an interpolated position for this result.
     *
     * @param partialTicks partial ticks of the currently rendered frame.
     * @return interpolated position of this result.
     */
    protected abstract Vec3d getPosition(final float partialTicks);

    /**
     * Override this to render a simple 2D overlay in the world at the position
     * of this result. The render state will be set up such that the rendered
     * 2D graphic will face the player and always have the same size, regardless
     * of the distance to the player.
     *
     * @param player       the entity we're rendering for. Usually the player.
     * @param playerPos    the interpolated position of the entity.
     * @param playerAngle  the interpolated entity yaw and pitch.
     * @param partialTicks partial ticks of the currently rendered frame.
     */
    protected void render2D(final Entity player, final Vec3d playerPos, final Vec2f playerAngle, final float partialTicks) {
        final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        final String value = getClass().getSimpleName();
        fontRenderer.drawString(value, -fontRenderer.getStringWidth(value), 0, 0xFFFFFFFF);
    }

    /**
     * Utility method for rendering a centered textured quad.
     *
     * @param width  the width of the quad.
     * @param height the height of the quad.
     */
    protected void renderQuad(final float width, final float height) {
        final Tessellator t = Tessellator.getInstance();
        final VertexBuffer buffer = t.getBuffer();

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        buffer.pos(-width / 2, height / 2, 0).tex(0, 1).endVertex();
        buffer.pos(width / 2, height / 2, 0).tex(1, 1).endVertex();
        buffer.pos(width / 2, -height / 2, 0).tex(1, 0).endVertex();
        buffer.pos(-width / 2, -height / 2, 0).tex(0, 0).endVertex();

        t.draw();
    }
}
