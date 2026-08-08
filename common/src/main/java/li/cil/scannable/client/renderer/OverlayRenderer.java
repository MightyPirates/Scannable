package li.cil.scannable.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import li.cil.scannable.api.API;
import li.cil.scannable.common.config.Strings;
import li.cil.scannable.common.item.ScannerItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public final class OverlayRenderer {
    private static final ResourceLocation PROGRESS = ResourceLocation.fromNamespaceAndPath(API.MOD_ID, "textures/gui/overlay/scanner_progress.png");

    public static void render(final GuiGraphics graphics, final float partialTick) {
        final Minecraft mc = Minecraft.getInstance();
        final Player player = mc.player;
        if (player == null) {
            return;
        }

        final ItemStack stack = player.getUseItem();
        if (stack.isEmpty()) {
            return;
        }

        if (!ScannerItem.isScanner(stack)) {
            return;
        }

        final int total = stack.getUseDuration(player);
        final int remaining = player.getUseItemRemainingTicks();

        final float progress = Mth.clamp(1 - (remaining - partialTick) / (float) total, 0, 1);

        final int screenWidth = mc.getWindow().getGuiScaledWidth();
        final int screenHeight = mc.getWindow().getGuiScaledHeight();

        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(0.66f, 0.8f, 0.93f, 0.66f);
        RenderSystem.setShaderTexture(0, PROGRESS);

        final BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_TEX);

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

        buffer.addVertex(midX, top, 0).setUv(0.5f, 1);
        if (progress < 0.125) { // Top right.
            buffer.addVertex(midX, midY, 0).setUv(0.5f, 0.5f);

            final float x = tx / ty * 0.5f;
            buffer.addVertex(midX + x * width, top, 0).setUv(0.5f + x, 1);
        } else {
            buffer.addVertex(midX, midY, 0).setUv(0.5f, 0.5f);
            buffer.addVertex(right, top, 0).setUv(1, 1);

            buffer.addVertex(right, top, 0).setUv(1, 1);
            if (progress < 0.375) { // Right.
                buffer.addVertex(midX, midY, 0).setUv(0.5f, 0.5f);

                final float y = Math.abs(ty / tx - 1) * 0.5f;
                buffer.addVertex(right, top + y * height, 0).setUv(1, 1 - y);
            } else {
                buffer.addVertex(midX, midY, 0).setUv(0.5f, 0.5f);
                buffer.addVertex(right, bottom, 0).setUv(1, 0);

                buffer.addVertex(right, bottom, 0).setUv(1, 0);
                if (progress < 0.625) { // Bottom.
                    buffer.addVertex(midX, midY, 0).setUv(0.5f, 0.5f);

                    final float x = Math.abs(tx / ty - 1) * 0.5f;
                    buffer.addVertex(left + x * width, bottom, 0).setUv(x, 0);
                } else {
                    buffer.addVertex(midX, midY, 0).setUv(0.5f, 0.5f);
                    buffer.addVertex(left, bottom, 0).setUv(0, 0);

                    buffer.addVertex(left, bottom, 0).setUv(0, 0);
                    if (progress < 0.875) { // Left.
                        buffer.addVertex(midX, midY, 0).setUv(0.5f, 0.5f);

                        final float y = (ty / tx + 1) * 0.5f;
                        buffer.addVertex(left, top + y * height, 0).setUv(0, 1 - y);
                    } else {
                        buffer.addVertex(midX, midY, 0).setUv(0.5f, 0.5f);
                        buffer.addVertex(left, top, 0).setUv(0, 1);

                        buffer.addVertex(left, top, 0).setUv(0, 1);
                        if (progress < 1) { // Top left.
                            buffer.addVertex(midX, midY, 0).setUv(0.5f, 0.5f);

                            final float x = Math.abs(tx / ty) * 0.5f;
                            buffer.addVertex(midX - x * width, top, 0).setUv(0.5f - x, 1);
                        } else {
                            buffer.addVertex(midX, midY, 0).setUv(0.5f, 0.5f);
                            buffer.addVertex(midX, top, 0).setUv(0.5f, 1);
                        }
                    }
                }
            }
        }

        BufferUploader.drawWithShader(buffer.buildOrThrow());

        final Component label = Strings.progress(Mth.floor(progress * 100));
        graphics.drawString(mc.font, label, right + 12, midY - mc.font.lineHeight / 2, 0xCCAACCEE, true);
    }
}
