package li.cil.scannable.client.renderer;

import com.mojang.blaze3d.pipeline.MainTarget;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import dev.architectury.injectables.annotations.ExpectPlatform;
import li.cil.scannable.client.ScanManager;
import li.cil.scannable.client.shader.Shaders;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL11.GL_NONE;
import static org.lwjgl.opengl.GL11.glDrawBuffer;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

@Environment(EnvType.CLIENT)
public enum ScannerRenderer {
    INSTANCE;

    // --------------------------------------------------------------------- //

    private DepthOnlyRenderTarget mainCameraDepth = new DepthOnlyRenderTarget(MainTarget.DEFAULT_WIDTH, MainTarget.DEFAULT_HEIGHT);

    // --------------------------------------------------------------------- //

    private long currentStart;
    private Vec3 currentCenter;

    // --------------------------------------------------------------------- //

    public void ping(final Vec3 pos) {
        currentStart = System.currentTimeMillis();
        currentCenter = pos;
    }

    public static void render(final Matrix4f viewMatrix, final Matrix4f projectionMatrix) {
        INSTANCE.doRender(viewMatrix, projectionMatrix);
    }

    private void doRender(final Matrix4f viewMatrix, final Matrix4f projectionMatrix) {
        if (shouldRender()) {
            grabDepthBuffer();
            renderEffect(viewMatrix, projectionMatrix);
        }
    }

    private boolean shouldRender() {
        final int adjustedDuration = ScanManager.computeScanGrowthDuration();
        return currentStart > 0 && adjustedDuration > (int) (System.currentTimeMillis() - currentStart);
    }

    private void grabDepthBuffer() {
        final RenderTarget mainRenderTarget = Minecraft.getInstance().getMainRenderTarget();
        if (mainRenderTarget.width != mainCameraDepth.width || mainRenderTarget.height != mainCameraDepth.height) {
            mainCameraDepth.resize(mainRenderTarget.width, mainRenderTarget.height, Minecraft.ON_OSX);
        }
        mainCameraDepth = ScannerRenderer.copyBufferSettings(mainRenderTarget, mainCameraDepth);
        mainCameraDepth.copyDepthFrom(mainRenderTarget);
        mainRenderTarget.bindWrite(false);
    }

    private void renderEffect(final Matrix4f viewMatrix, final Matrix4f projectionMatrix) {
        final ShaderInstance shader = Shaders.getScanEffectShader();
        if (shader == null) {
            return;
        }

        final RenderTarget target = Minecraft.getInstance().getMainRenderTarget();

        updateShaderUniforms(shader, viewMatrix, projectionMatrix);

        blit(target);
    }

    private void updateShaderUniforms(final ShaderInstance shader, final Matrix4f viewMatrix, final Matrix4f projectionMatrix) {
        final Matrix4f invertedViewMatrix = new Matrix4f(viewMatrix);
        invertedViewMatrix.invert();

        // Must be the projection used for level rendering; RenderSystem's current
        // projection is not guaranteed to be that at the point we render from.
        final Matrix4f invertedProjectionMatrix = new Matrix4f(projectionMatrix);
        invertedProjectionMatrix.invert();

        final Vec3 cameraPosition = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

        final int adjustedDuration = ScanManager.computeScanGrowthDuration();
        final float radius = ScanManager.computeRadius(currentStart, (float) adjustedDuration);

        shader.setSampler("depthTex", mainCameraDepth.getDepthTextureId());
        shader.safeGetUniform("center").set(currentCenter.toVector3f());
        shader.safeGetUniform("invViewMat").set(invertedViewMatrix);
        shader.safeGetUniform("invProjMat").set(invertedProjectionMatrix);
        shader.safeGetUniform("pos").set(cameraPosition.toVector3f());
        shader.safeGetUniform("radius").set(radius);
    }

    private void blit(final RenderTarget target) {
        final int width = target.width;
        final int height = target.height;

        RenderSystem.depthMask(false);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();

        final ShaderInstance oldShader = RenderSystem.getShader();
        RenderSystem.setShader(Shaders::getScanEffectShader);

        RenderSystem.backupProjectionMatrix();
        RenderSystem.setProjectionMatrix(new Matrix4f().setOrtho(0, width, 0, height, 1, 100), VertexSorting.ORTHOGRAPHIC_Z);

        // This is a screen space quad, so it must not inherit the camera transform.
        // Depending on which hook we render from, the model view matrix may still hold
        // it: as of MC 1.21 LevelRenderer.renderLevel pushes the camera transform onto
        // the model view stack and only pops it at the very end. Fabric's
        // WorldRenderEvents.LAST fires before that pop, NeoForge's AFTER_LEVEL after it.
        RenderSystem.getModelViewStack().pushMatrix().identity();
        RenderSystem.applyModelViewMatrix();

        final BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.addVertex(0, 0, -50).setUv(0, 0);
        buffer.addVertex(width, 0, -50).setUv(1, 0);
        buffer.addVertex(width, height, -50).setUv(1, 1);
        buffer.addVertex(0, height, -50).setUv(0, 1);
        BufferUploader.drawWithShader(buffer.buildOrThrow());

        RenderSystem.getModelViewStack().popMatrix();
        RenderSystem.applyModelViewMatrix();

        RenderSystem.restoreProjectionMatrix();

        RenderSystem.setShader(() -> oldShader);

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    // --------------------------------------------------------------------- //

    public static final class DepthOnlyRenderTarget extends TextureTarget {
        public DepthOnlyRenderTarget(final int width, final int height) {
            super(width, height, true, Minecraft.ON_OSX);
        }

        @Override
        public void createBuffers(final int width, final int height, final boolean isOnOSX) {
            super.createBuffers(width, height, isOnOSX);
            if (colorTextureId > -1) {
                if (frameBufferId > -1) {
                    glBindFramebuffer(GL_FRAMEBUFFER, frameBufferId);
                    glDrawBuffer(GL_NONE);
                    glBindFramebuffer(GL_FRAMEBUFFER, 0);
                }
                TextureUtil.releaseTextureId(this.colorTextureId);
                this.colorTextureId = -1;
            }
        }
    }

    @SuppressWarnings("PMD.UnusedFormalParameter")
    @ExpectPlatform
    private static DepthOnlyRenderTarget copyBufferSettings(final RenderTarget mainRenderTarget, final DepthOnlyRenderTarget depthRenderTarget) {
        throw new AssertionError();
    }
}
