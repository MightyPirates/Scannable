package li.cil.scannable.client.renderer;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import org.joml.Matrix3x2f;

import javax.annotation.Nullable;

public record ScannerProgressRenderState(
    RenderPipeline pipeline,
    TextureSetup textureSetup,
    Matrix3x2f pose,
    int midX,
    int midY,
    int size,
    float progress,
    int color,
    @Nullable ScreenRectangle scissorArea,
    @Nullable ScreenRectangle bounds
) implements GuiElementRenderState {
    public ScannerProgressRenderState(
        final RenderPipeline pipeline,
        final TextureSetup textureSetup,
        final Matrix3x2f pose,
        final int midX,
        final int midY,
        final int size,
        final float progress,
        final int color,
        @Nullable final ScreenRectangle scissorArea
    ) {
        this(pipeline, textureSetup, pose, midX, midY, size, progress, color, scissorArea,
            getBounds(midX, midY, size, pose, scissorArea));
    }

    @Override
    public void buildVertices(final VertexConsumer consumer) {
        final int half = size / 2;
        final int left = midX - half;
        final int right = midX + half;
        final int top = midY - half;
        final int bottom = midY + half;

        final float[] boundary = new float[14 * 4];
        int count = 0;

        count = push(boundary, count, midX, top, 0.5f, 1);
        if (progress >= 0.125f) {
            count = push(boundary, count, right, top, 1, 1);
        }
        if (progress >= 0.375f) {
            count = push(boundary, count, right, bottom, 1, 0);
        }
        if (progress >= 0.625f) {
            count = push(boundary, count, left, bottom, 0, 0);
        }
        if (progress >= 0.875f) {
            count = push(boundary, count, left, top, 0, 1);
        }

        final float angle = (float) (progress * Math.PI * 2);
        final float tx = (float) Math.sin(angle);
        final float ty = (float) Math.cos(angle);

        if (progress < 0.125f) { // Top right.
            final float x = tx / ty * 0.5f;
            count = push(boundary, count, midX + x * size, top, 0.5f + x, 1);
        } else if (progress < 0.375f) { // Right.
            final float y = Math.abs(ty / tx - 1) * 0.5f;
            count = push(boundary, count, right, top + y * size, 1, 1 - y);
        } else if (progress < 0.625f) { // Bottom.
            final float x = Math.abs(tx / ty - 1) * 0.5f;
            count = push(boundary, count, left + x * size, bottom, x, 0);
        } else if (progress < 0.875f) { // Left.
            final float y = (ty / tx + 1) * 0.5f;
            count = push(boundary, count, left, top + y * size, 0, 1 - y);
        } else if (progress < 1) { // Top left.
            final float x = Math.abs(tx / ty) * 0.5f;
            count = push(boundary, count, midX - x * size, top, 0.5f - x, 1);
        } else {
            count = push(boundary, count, midX, top, 0.5f, 1);
        }

        for (int i = 0; i + 1 < count; i++) {
            final int a = i * 4;
            final int b = (i + 1) * 4;

            vertex(consumer, boundary[a], boundary[a + 1], boundary[a + 2], boundary[a + 3]);
            vertex(consumer, midX, midY, 0.5f, 0.5f);
            vertex(consumer, boundary[b], boundary[b + 1], boundary[b + 2], boundary[b + 3]);
            vertex(consumer, boundary[b], boundary[b + 1], boundary[b + 2], boundary[b + 3]);
        }
    }

    private void vertex(final VertexConsumer consumer, final float x, final float y, final float u, final float v) {
        consumer.addVertexWith2DPose(pose, x, y).setUv(u, v).setColor(color);
    }

    private static int push(final float[] target, final int count, final float x, final float y, final float u, final float v) {
        final int offset = count * 4;
        target[offset] = x;
        target[offset + 1] = y;
        target[offset + 2] = u;
        target[offset + 3] = v;
        return count + 1;
    }

    @Nullable
    private static ScreenRectangle getBounds(final int midX, final int midY, final int size, final Matrix3x2f pose, @Nullable final ScreenRectangle scissorArea) {
        final int half = size / 2;
        final ScreenRectangle rectangle = new ScreenRectangle(midX - half, midY - half, size, size).transformMaxBounds(pose);
        return scissorArea != null ? scissorArea.intersection(rectangle) : rectangle;
    }
}
