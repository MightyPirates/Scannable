package li.cil.scannable.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import li.cil.scannable.client.ScanManager;
import li.cil.scannable.client.shader.ScanEffectShader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.FramebufferConstants;
import net.minecraft.client.util.JSONBlendingMode;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;

@OnlyIn(Dist.CLIENT)
public enum ScannerRenderer {
    INSTANCE;

    // --------------------------------------------------------------------- //
    // Settings

    // --------------------------------------------------------------------- //
    // Framebuffer and depth texture IDs.

    private int depthCopyFbo;
    private int depthCopyColorBuffer;
    private int depthCopyDepthBuffer;

    // --------------------------------------------------------------------- //
    // Effect shader and uniforms.

    private static final JSONBlendingMode RESET_BLEND_STATE = new JSONBlendingMode();

    // --------------------------------------------------------------------- //
    // State of the scanner, set when triggering a ping.

    private long currentStart;

    // --------------------------------------------------------------------- //

    public void ping(final Vector3d pos) {
        currentStart = System.currentTimeMillis();
        ScanEffectShader.INSTANCE.setCenter(pos);
    }

    @SubscribeEvent
    public void onWorldRender(final RenderWorldLastEvent event) {
        final int adjustedDuration = ScanManager.computeScanGrowthDuration();
        final boolean shouldRender = currentStart > 0 && adjustedDuration > (int) (System.currentTimeMillis() - currentStart);
        if (shouldRender) {
            if (depthCopyFbo == 0) {
                createDepthCopyFramebuffer();
            }

            render(event.getMatrixStack().getLast().getMatrix(), event.getProjectionMatrix());
        } else {
            if (depthCopyFbo != 0) {
                deleteDepthCopyFramebuffer();
            }

            currentStart = 0;
        }
    }

    private void render(final Matrix4f viewMatrix, final Matrix4f projectionMatrix) {
        final Minecraft mc = Minecraft.getInstance();
        final Framebuffer framebuffer = mc.getFramebuffer();

        updateDepthTexture(framebuffer);

        final Matrix4f invertedViewMatrix = new Matrix4f(viewMatrix);
        invertedViewMatrix.invert();
        ScanEffectShader.INSTANCE.setInverseViewMatrix(invertedViewMatrix);

        final Matrix4f invertedProjectionMatrix = new Matrix4f(projectionMatrix);
        invertedProjectionMatrix.invert();
        ScanEffectShader.INSTANCE.setInverseProjectionMatrix(invertedProjectionMatrix);

        final Vector3d position = mc.gameRenderer.getActiveRenderInfo().getProjectedView();
        ScanEffectShader.INSTANCE.setPosition(position);

        final int adjustedDuration = ScanManager.computeScanGrowthDuration();
        final float radius = ScanManager.computeRadius(currentStart, (float) adjustedDuration);
        ScanEffectShader.INSTANCE.setRadius(radius);

        // This is a bit of a hack; blend state is changed from many places in MC, and if there's
        // no other shader active at all, our shader will not properly apply its blend settings.
        // So we use a dummy blend state to change the reference to the last applied one to force it.
        RESET_BLEND_STATE.apply();

        ScanEffectShader.INSTANCE.bind();

        blit(framebuffer);

        ScanEffectShader.INSTANCE.unbind();
    }

    private void updateDepthTexture(final Framebuffer framebuffer) {
        GlStateManager.bindFramebuffer(GL30.GL_READ_FRAMEBUFFER, framebuffer.framebufferObject);
        GlStateManager.bindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, depthCopyFbo);
        GL30.glBlitFramebuffer(0, 0, framebuffer.framebufferTextureWidth, framebuffer.framebufferTextureHeight,
                0, 0, framebuffer.framebufferTextureWidth, framebuffer.framebufferTextureHeight,
                GL30.GL_DEPTH_BUFFER_BIT, GL30.GL_NEAREST);
    }

    // --------------------------------------------------------------------- //

    private void createDepthCopyFramebuffer() {
        final Framebuffer framebuffer = Minecraft.getInstance().getFramebuffer();

        depthCopyFbo = GlStateManager.genFramebuffers();

        // We don't use the color attachment on this FBO, but it's required for a complete FBO.
        depthCopyColorBuffer = createTexture(framebuffer.framebufferTextureWidth, framebuffer.framebufferTextureHeight, GL11.GL_RGBA8, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE);

        // Main reason why we create this FBO: readable depth buffer into which we can copy the MC one.
        depthCopyDepthBuffer = createTexture(framebuffer.framebufferTextureWidth, framebuffer.framebufferTextureHeight, GL30.GL_DEPTH_COMPONENT, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT);

        GlStateManager.bindFramebuffer(FramebufferConstants.GL_FRAMEBUFFER, depthCopyFbo);
        GlStateManager.framebufferTexture2D(FramebufferConstants.GL_FRAMEBUFFER, FramebufferConstants.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, depthCopyColorBuffer, 0);
        GlStateManager.framebufferTexture2D(FramebufferConstants.GL_FRAMEBUFFER, FramebufferConstants.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, depthCopyDepthBuffer, 0);
        GlStateManager.bindFramebuffer(FramebufferConstants.GL_FRAMEBUFFER, 0);

        ScanEffectShader.INSTANCE.setDepthBuffer(depthCopyDepthBuffer);
    }

    private void deleteDepthCopyFramebuffer() {
        ScanEffectShader.INSTANCE.setDepthBuffer(0);

        GlStateManager.deleteFramebuffers(depthCopyFbo);
        depthCopyFbo = 0;

        TextureUtil.releaseTextureId(depthCopyColorBuffer);
        depthCopyColorBuffer = 0;

        TextureUtil.releaseTextureId(depthCopyDepthBuffer);
        depthCopyDepthBuffer = 0;
    }

    private int createTexture(final int width, final int height, final int internalFormat, final int format, final int type) {
        final int texture = TextureUtil.generateTextureId();
        GlStateManager.bindTexture(texture);
        GlStateManager.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GlStateManager.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GlStateManager.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GlStateManager.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GlStateManager.texParameter(GL11.GL_TEXTURE_2D, GL14.GL_DEPTH_TEXTURE_MODE, GL11.GL_LUMINANCE);
        GlStateManager.texParameter(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_COMPARE_MODE, GL14.GL_NONE);
        GlStateManager.texParameter(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_COMPARE_FUNC, GL11.GL_LEQUAL);
        GlStateManager.texImage2D(GL11.GL_TEXTURE_2D, 0, internalFormat, width, height, 0, format, type, null);
        GlStateManager.bindTexture(0);
        return texture;
    }

    private void blit(final Framebuffer framebuffer) {
        final int width = framebuffer.framebufferTextureWidth;
        final int height = framebuffer.framebufferTextureHeight;

        RenderSystem.depthMask(false);
        RenderSystem.disableDepthTest();

        setupMatrices(width, height);

        framebuffer.bindFramebuffer(false);

        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(0, height, 0).tex(0, 0).endVertex();
        buffer.pos(width, height, 0).tex(1, 0).endVertex();
        buffer.pos(width, 0, 0).tex(1, 1).endVertex();
        buffer.pos(0, 0, 0).tex(0, 1).endVertex();
        tessellator.draw();

        restoreMatrices();

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }

    private void setupMatrices(final int width, final int height) {
        RenderSystem.matrixMode(GL11.GL_PROJECTION);
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        RenderSystem.ortho(0, width, height, 0, 1000, 3000);
        RenderSystem.matrixMode(GL11.GL_MODELVIEW);
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        RenderSystem.translated(0, 0, -2000);
        RenderSystem.viewport(0, 0, width, height);
    }

    private void restoreMatrices() {
        RenderSystem.matrixMode(GL11.GL_PROJECTION);
        RenderSystem.popMatrix();
        RenderSystem.matrixMode(GL11.GL_MODELVIEW);
        RenderSystem.popMatrix();
    }
}
