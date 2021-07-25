package li.cil.scannable.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import li.cil.scannable.api.API;
import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.item.ItemScanner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

@OnlyIn(Dist.CLIENT)
public final class OverlayRenderer {
    private static final ResourceLocation PROGRESS = new ResourceLocation(API.MOD_ID, "textures/gui/overlay/scanner_progress.png");

    public static void onOverlayRender(final RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        final Minecraft mc = Minecraft.getInstance();
        final Player player = mc.player;
        if (player == null) {
            return;
        }

        final ItemStack stack = player.getUseItem();
        if (stack.isEmpty()) {
            return;
        }

        if (!ItemScanner.isScanner(stack)) {
            return;
        }

        final int total = stack.getUseDuration();
        final int remaining = player.getUseItemRemainingTicks();

        final float progress = Mth.clamp(1 - (remaining - event.getPartialTicks()) / (float) total, 0, 1);

        final int screenWidth = mc.getWindow().getGuiScaledWidth();
        final int screenHeight = mc.getWindow().getGuiScaledHeight();

        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(0.66f, 0.8f, 0.93f, 0.66f);
        RenderSystem.setShaderTexture(0, PROGRESS);

        final Tesselator buffer = Tesselator.getInstance();
        final BufferBuilder builder = buffer.getBuilder();

        builder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_TEX);

        final int width = 64;
        final int height = 64;
        final int midX = screenWidth / 2;
        final int midY = screenHeight / 2;
        final int left = midX - width / 2;
        final int right = midX + width / 2;
        final int top = midY - height / 2;
        final int bottom = midY + height / 2;

        final float angle = (float) (progress * Math.PI * 2);
        final float tx = Mth.sin(angle);
        final float ty = Mth.cos(angle);

        builder.vertex(midX, top, 0).uv(0.5f, 1).endVertex();
        if (progress < 0.125) { // Top right.
            builder.vertex(midX, midY, 0).uv(0.5f, 0.5f).endVertex();

            final float x = tx / ty * 0.5f;
            builder.vertex(midX + x * width, top, 0).uv(0.5f + x, 1).endVertex();
        } else {
            builder.vertex(midX, midY, 0).uv(0.5f, 0.5f).endVertex();
            builder.vertex(right, top, 0).uv(1, 1).endVertex();

            builder.vertex(right, top, 0).uv(1, 1).endVertex();
            if (progress < 0.375) { // Right.
                builder.vertex(midX, midY, 0).uv(0.5f, 0.5f).endVertex();

                final float y = Math.abs(ty / tx - 1) * 0.5f;
                builder.vertex(right, top + y * height, 0).uv(1, 1 - y).endVertex();
            } else {
                builder.vertex(midX, midY, 0).uv(0.5f, 0.5f).endVertex();
                builder.vertex(right, bottom, 0).uv(1, 0).endVertex();

                builder.vertex(right, bottom, 0).uv(1, 0).endVertex();
                if (progress < 0.625) { // Bottom.
                    builder.vertex(midX, midY, 0).uv(0.5f, 0.5f).endVertex();

                    final float x = Math.abs(tx / ty - 1) * 0.5f;
                    builder.vertex(left + x * width, bottom, 0).uv(x, 0).endVertex();
                } else {
                    builder.vertex(midX, midY, 0).uv(0.5f, 0.5f).endVertex();
                    builder.vertex(left, bottom, 0).uv(0, 0).endVertex();

                    builder.vertex(left, bottom, 0).uv(0, 0).endVertex();
                    if (progress < 0.875) { // Left.
                        builder.vertex(midX, midY, 0).uv(0.5f, 0.5f).endVertex();

                        final float y = (ty / tx + 1) * 0.5f;
                        builder.vertex(left, top + y * height, 0).uv(0, 1 - y).endVertex();
                    } else {
                        builder.vertex(midX, midY, 0).uv(0.5f, 0.5f).endVertex();
                        builder.vertex(left, top, 0).uv(0, 1).endVertex();

                        builder.vertex(left, top, 0).uv(0, 1).endVertex();
                        if (progress < 1) { // Top left.
                            builder.vertex(midX, midY, 0).uv(0.5f, 0.5f).endVertex();

                            final float x = Math.abs(tx / ty) * 0.5f;
                            builder.vertex(midX - x * width, top, 0).uv(0.5f - x, 1).endVertex();
                        } else {
                            builder.vertex(midX, midY, 0).uv(0.5f, 0.5f).endVertex();
                            builder.vertex(midX, top, 0).uv(0.5f, 1).endVertex();
                        }
                    }
                }
            }
        }

        buffer.end();

        final TranslatableComponent label = new TranslatableComponent(Constants.GUI_SCANNER_PROGRESS, Mth.floor(progress * 100));
        mc.font.drawShadow(event.getMatrixStack(), label, right + 12, midY - mc.font.lineHeight * 0.5f, 0xCCAACCEE);
    }
}
