/* SPDX-License-Identifier: MIT */

package li.cil.scannable.client.renderer;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import li.cil.scannable.client.ScanManager;
import li.cil.scannable.client.shader.ScanPipelines;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

import javax.annotation.Nullable;
import java.util.OptionalDouble;
import java.util.OptionalInt;

public enum ScannerRenderer {
    INSTANCE;

    // --------------------------------------------------------------------- //

    // See scan_effect.fsh.
    private static final int UNIFORM_SIZE = new Std140SizeCalculator()
        .putMat4f()
        .putMat4f()
        .putVec4()
        .putVec4()
        .get();

    // --------------------------------------------------------------------- //

    @Nullable
    private GpuBuffer uniformBuffer;

    // --------------------------------------------------------------------- //

    private long currentStart;
    private Vec3 currentCenter = Vec3.ZERO;

    // --------------------------------------------------------------------- //

    public void ping(final Vec3 pos) {
        currentStart = System.currentTimeMillis();
        currentCenter = pos;
    }

    public static void render(final Matrix4f viewMatrix, final Matrix4f projectionMatrix) {
        INSTANCE.doRender(viewMatrix, projectionMatrix);
    }

    private void doRender(final Matrix4f viewMatrix, final Matrix4f projectionMatrix) {
        if (!shouldRender()) {
            return;
        }

        final RenderTarget target = Minecraft.getInstance().getMainRenderTarget();
        final GpuTextureView depth = target.getDepthTextureView();
        if (depth == null) {
            return;
        }

        updateUniforms(viewMatrix, projectionMatrix);

        final CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();

        // No depth of our own, we render the effect just based on the existing depth.
        try (RenderPass renderPass = commandEncoder.createRenderPass(
            () -> "Scannable scan effect",
            target.getColorTextureView(),
            OptionalInt.empty(),
            null,
            OptionalDouble.empty())) {
            renderPass.setPipeline(ScanPipelines.SCAN_EFFECT);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform(ScanPipelines.SCAN_EFFECT_UNIFORM, uniformBuffer);
            renderPass.bindTexture(
                ScanPipelines.SCAN_EFFECT_DEPTH_SAMPLER,
                depth,
                RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST));
            renderPass.draw(0, 3);
        }
    }

    private boolean shouldRender() {
        final int adjustedDuration = ScanManager.computeScanGrowthDuration();
        return currentStart > 0 && adjustedDuration > (int) (System.currentTimeMillis() - currentStart);
    }

    private void updateUniforms(final Matrix4f viewMatrix, final Matrix4f projectionMatrix) {
        final Matrix4f invertedViewMatrix = new Matrix4f(viewMatrix).invert();

        // Must be the projection used for level rendering; the currently bound
        // projection is not guaranteed to be that at the point we render from.
        final Matrix4f invertedProjectionMatrix = new Matrix4f(projectionMatrix).invert();

        final Vec3 cameraPosition = Minecraft.getInstance().gameRenderer.getMainCamera().position();

        final int adjustedDuration = ScanManager.computeScanGrowthDuration();
        final float radius = ScanManager.computeRadius(currentStart, (float) adjustedDuration);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            final Std140Builder builder = Std140Builder.onStack(stack, UNIFORM_SIZE)
                .putMat4f(invertedViewMatrix)
                .putMat4f(invertedProjectionMatrix)
                .putVec4((float) cameraPosition.x, (float) cameraPosition.y, (float) cameraPosition.z, 0)
                .putVec4((float) currentCenter.x, (float) currentCenter.y, (float) currentCenter.z, radius);

            if (uniformBuffer == null) {
                uniformBuffer = RenderSystem.getDevice().createBuffer(
                    () -> "Scannable scan effect uniforms",
                    GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_COPY_DST,
                    builder.get());
            } else {
                RenderSystem.getDevice().createCommandEncoder().writeToBuffer(uniformBuffer.slice(), builder.get());
            }
        }
    }
}
