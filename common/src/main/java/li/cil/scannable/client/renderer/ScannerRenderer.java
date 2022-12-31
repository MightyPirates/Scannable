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

    public static void render(final PoseStack poseStack) {
        INSTANCE.doRender(poseStack);
    }

    public void doRender(final PoseStack poseStack) {
        if (shouldRender()) {
            grabDepthBuffer();
            render(poseStack.last().pose());
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

    private void render(final Matrix4f viewMatrix) {
        final ShaderInstance shader = Shaders.getScanEffectShader();
        if (shader == null) {
            return;
        }

        final RenderTarget target = Minecraft.getInstance().getMainRenderTarget();

        updateShaderUniforms(shader, viewMatrix);

        blit(target);
    }

    private void updateShaderUniforms(final ShaderInstance shader, final Matrix4f viewMatrix) {
        final Matrix4f invertedViewMatrix = new Matrix4f(viewMatrix);
        invertedViewMatrix.invert();

        final Matrix4f invertedProjectionMatrix = new Matrix4f(RenderSystem.getProjectionMatrix());
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
        RenderSystem.setProjectionMatrix(new Matrix4f().setOrtho(0, width, 0, height, 1, 100));

        final Tesselator tesselator = Tesselator.getInstance();
        final BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(0, 0, -50).uv(0, 0).endVertex();
        buffer.vertex(width, 0, -50).uv(1, 0).endVertex();
        buffer.vertex(width, height, -50).uv(1, 1).endVertex();
        buffer.vertex(0, height, -50).uv(0, 1).endVertex();
        tesselator.end();

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

    @ExpectPlatform
    private static DepthOnlyRenderTarget copyBufferSettings(final RenderTarget mainRenderTarget, final DepthOnlyRenderTarget depthRenderTarget) {
        throw new AssertionError();
    }
}
