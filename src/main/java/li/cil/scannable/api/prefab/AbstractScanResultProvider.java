package li.cil.scannable.api.prefab;

import li.cil.scannable.api.scanning.ScanResult;
import li.cil.scannable.api.scanning.ScanResultProvider;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.Collection;

/**
 * Helper base class for scan result providers, providing some common
 * functionality for drawing result information.
 */
public abstract class AbstractScanResultProvider implements ScanResultProvider {
    protected EntityPlayer player;
    protected Vec3d center;
    protected float radius;

    // --------------------------------------------------------------------- //
    // ScanResultProvider

    @Override
    public int getEnergyCost(final EntityPlayer player, final ItemStack module) {
        return 50;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void initialize(final EntityPlayer player, final Collection<ItemStack> modules, final Vec3d center, final float radius, final int scanTicks) {
        this.player = player;
        this.center = center;
        this.radius = radius;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean isValid(final ScanResult result) {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void reset() {
        player = null;
        center = null;
        radius = 0f;
    }

    // --------------------------------------------------------------------- //

    /**
     * Utility method for rendering a centered textured quad.
     *
     * @param width  the width of the quad.
     * @param height the height of the quad.
     */
    @SideOnly(Side.CLIENT)
    protected static void renderQuad(final float width, final float height) {
        final Tessellator t = Tessellator.getInstance();
        final VertexBuffer buffer = t.getBuffer();

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        drawTexturedQuad(width, height, buffer);

        t.draw();
    }

    // --------------------------------------------------------------------- //
    // Drawing simple primitives in an existing buffer.

    @SideOnly(Side.CLIENT)
    protected static void drawTexturedQuad(final float width, final float height, final VertexBuffer buffer) {
        buffer.pos(-width / 2, height / 2, 0).tex(0, 1).endVertex();
        buffer.pos(width / 2, height / 2, 0).tex(1, 1).endVertex();
        buffer.pos(width / 2, -height / 2, 0).tex(1, 0).endVertex();
        buffer.pos(-width / 2, -height / 2, 0).tex(0, 0).endVertex();
    }

    @SideOnly(Side.CLIENT)
    protected static void drawCube(final double minX, final double minY, final double minZ, final double maxX, final double maxY, final double maxZ, final VertexBuffer buffer) {
        drawPlaneNegX(minX, minY, maxY, minZ, maxZ, buffer);
        drawPlanePosX(maxX, minY, maxY, minZ, maxZ, buffer);
        drawPlaneNegY(minY, minX, maxX, minZ, maxZ, buffer);
        drawPlanePosY(maxY, minX, maxX, minZ, maxZ, buffer);
        drawPlaneNegZ(minZ, minX, maxX, minY, maxY, buffer);
        drawPlanePosZ(maxZ, minX, maxX, minY, maxY, buffer);
    }

    @SideOnly(Side.CLIENT)
    protected static void drawCube(final double minX, final double minY, final double minZ, final double maxX, final double maxY, final double maxZ, final float r, final float g, final float b, final float a, final VertexBuffer buffer) {
        drawPlaneNegX(minX, minY, maxY, minZ, maxZ, r, g, b, a, buffer);
        drawPlanePosX(maxX, minY, maxY, minZ, maxZ, r, g, b, a, buffer);
        drawPlaneNegY(minY, minX, maxX, minZ, maxZ, r, g, b, a, buffer);
        drawPlanePosY(maxY, minX, maxX, minZ, maxZ, r, g, b, a, buffer);
        drawPlaneNegZ(minZ, minX, maxX, minY, maxY, r, g, b, a, buffer);
        drawPlanePosZ(maxZ, minX, maxX, minY, maxY, r, g, b, a, buffer);
    }

    @SideOnly(Side.CLIENT)
    protected static void drawPlaneNegX(final double x, final double minY, final double maxY, final double minZ, final double maxZ, final VertexBuffer buffer) {
        buffer.pos(x, minY, minZ).endVertex();
        buffer.pos(x, minY, maxZ).endVertex();
        buffer.pos(x, maxY, maxZ).endVertex();
        buffer.pos(x, maxY, minZ).endVertex();
    }

    @SideOnly(Side.CLIENT)
    protected static void drawPlaneNegX(final double x, final double minY, final double maxY, final double minZ, final double maxZ, final float r, final float g, final float b, final float a, final VertexBuffer buffer) {
        buffer.pos(x, minY, minZ).color(r, g, b, a).endVertex();
        buffer.pos(x, minY, maxZ).color(r, g, b, a).endVertex();
        buffer.pos(x, maxY, maxZ).color(r, g, b, a).endVertex();
        buffer.pos(x, maxY, minZ).color(r, g, b, a).endVertex();
    }

    @SideOnly(Side.CLIENT)
    protected static void drawPlanePosX(final double x, final double minY, final double maxY, final double minZ, final double maxZ, final VertexBuffer buffer) {
        buffer.pos(x, minY, minZ).endVertex();
        buffer.pos(x, maxY, minZ).endVertex();
        buffer.pos(x, maxY, maxZ).endVertex();
        buffer.pos(x, minY, maxZ).endVertex();
    }

    @SideOnly(Side.CLIENT)
    protected static void drawPlanePosX(final double x, final double minY, final double maxY, final double minZ, final double maxZ, final float r, final float g, final float b, final float a, final VertexBuffer buffer) {
        buffer.pos(x, minY, minZ).color(r, g, b, a).endVertex();
        buffer.pos(x, maxY, minZ).color(r, g, b, a).endVertex();
        buffer.pos(x, maxY, maxZ).color(r, g, b, a).endVertex();
        buffer.pos(x, minY, maxZ).color(r, g, b, a).endVertex();
    }

    @SideOnly(Side.CLIENT)
    protected static void drawPlaneNegY(final double y, final double minX, final double maxX, final double minZ, final double maxZ, final VertexBuffer buffer) {
        buffer.pos(minX, y, minZ).endVertex();
        buffer.pos(maxX, y, minZ).endVertex();
        buffer.pos(maxX, y, maxZ).endVertex();
        buffer.pos(minX, y, maxZ).endVertex();
    }

    @SideOnly(Side.CLIENT)
    protected static void drawPlaneNegY(final double y, final double minX, final double maxX, final double minZ, final double maxZ, final float r, final float g, final float b, final float a, final VertexBuffer buffer) {
        buffer.pos(minX, y, minZ).color(r, g, b, a).endVertex();
        buffer.pos(maxX, y, minZ).color(r, g, b, a).endVertex();
        buffer.pos(maxX, y, maxZ).color(r, g, b, a).endVertex();
        buffer.pos(minX, y, maxZ).color(r, g, b, a).endVertex();
    }

    @SideOnly(Side.CLIENT)
    protected static void drawPlanePosY(final double y, final double minX, final double maxX, final double minZ, final double maxZ, final VertexBuffer buffer) {
        buffer.pos(minX, y, minZ).endVertex();
        buffer.pos(minX, y, maxZ).endVertex();
        buffer.pos(maxX, y, maxZ).endVertex();
        buffer.pos(maxX, y, minZ).endVertex();
    }

    @SideOnly(Side.CLIENT)
    protected static void drawPlanePosY(final double y, final double minX, final double maxX, final double minZ, final double maxZ, final float r, final float g, final float b, final float a, final VertexBuffer buffer) {
        buffer.pos(minX, y, minZ).color(r, g, b, a).endVertex();
        buffer.pos(minX, y, maxZ).color(r, g, b, a).endVertex();
        buffer.pos(maxX, y, maxZ).color(r, g, b, a).endVertex();
        buffer.pos(maxX, y, minZ).color(r, g, b, a).endVertex();
    }

    @SideOnly(Side.CLIENT)
    protected static void drawPlaneNegZ(final double z, final double minX, final double maxX, final double minY, final double maxY, final VertexBuffer buffer) {
        buffer.pos(minX, minY, z).endVertex();
        buffer.pos(minX, maxY, z).endVertex();
        buffer.pos(maxX, maxY, z).endVertex();
        buffer.pos(maxX, minY, z).endVertex();
    }

    @SideOnly(Side.CLIENT)
    protected static void drawPlaneNegZ(final double z, final double minX, final double maxX, final double minY, final double maxY, final float r, final float g, final float b, final float a, final VertexBuffer buffer) {
        buffer.pos(minX, minY, z).color(r, g, b, a).endVertex();
        buffer.pos(minX, maxY, z).color(r, g, b, a).endVertex();
        buffer.pos(maxX, maxY, z).color(r, g, b, a).endVertex();
        buffer.pos(maxX, minY, z).color(r, g, b, a).endVertex();
    }

    @SideOnly(Side.CLIENT)
    protected static void drawPlanePosZ(final double z, final double minX, final double maxX, final double minY, final double maxY, final VertexBuffer buffer) {
        buffer.pos(minX, minY, z).endVertex();
        buffer.pos(maxX, minY, z).endVertex();
        buffer.pos(maxX, maxY, z).endVertex();
        buffer.pos(minX, maxY, z).endVertex();
    }

    @SideOnly(Side.CLIENT)
    protected static void drawPlanePosZ(final double z, final double minX, final double maxX, final double minY, final double maxY, final float r, final float g, final float b, final float a, final VertexBuffer buffer) {
        buffer.pos(minX, minY, z).color(r, g, b, a).endVertex();
        buffer.pos(maxX, minY, z).color(r, g, b, a).endVertex();
        buffer.pos(maxX, maxY, z).color(r, g, b, a).endVertex();
        buffer.pos(minX, maxY, z).color(r, g, b, a).endVertex();
    }
}
