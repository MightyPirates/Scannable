package li.cil.scannable.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import li.cil.scannable.api.API;
import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.item.ItemScanner;
import li.cil.scannable.util.Migration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public enum OverlayRenderer {
    INSTANCE;

    private static final ResourceLocation PROGRESS = new ResourceLocation(API.MOD_ID, "textures/gui/overlay/scanner_progress.png");

    @SubscribeEvent
    public void onOverlayRender(final RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        final Minecraft mc = Minecraft.getInstance();
        final PlayerEntity player = mc.player;
        if (player == null) {
            return;
        }

        final ItemStack stack = player.getActiveItemStack();
        if (stack.isEmpty()) {
            return;
        }

        if (!ItemScanner.isScanner(stack)) {
            return;
        }

        final int total = stack.getUseDuration();
        final int remaining = player.getItemInUseCount();

        final float progress = MathHelper.clamp(1 - (remaining - event.getPartialTicks()) / (float) total, 0, 1);

        final int screenWidth = mc.getMainWindow().getScaledWidth();
        final int screenHeight = mc.getMainWindow().getScaledHeight();

        RenderSystem.enableBlend();
        RenderSystem.color4f(0.66f, 0.8f, 0.93f, 0.66f);
        mc.getTextureManager().bindTexture(PROGRESS);

        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder buffer = tessellator.getBuffer();

        buffer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);

        final int width = 64;
        final int height = 64;
        final int midX = screenWidth / 2;
        final int midY = screenHeight / 2;
        final int left = midX - width / 2;
        final int right = midX + width / 2;
        final int top = midY - height / 2;
        final int bottom = midY + height / 2;

        final float angle = (float) (progress * Math.PI * 2);
        final float tx = MathHelper.sin(angle);
        final float ty = MathHelper.cos(angle);

        buffer.pos(midX, top, 0).tex(0.5f, 1).endVertex();
        if (progress < 0.125) { // Top right.
            buffer.pos(midX, midY, 0).tex(0.5f, 0.5f).endVertex();

            final float x = tx / ty * 0.5f;
            buffer.pos(midX + x * width, top, 0).tex(0.5f + x, 1).endVertex();
        } else {
            buffer.pos(midX, midY, 0).tex(0.5f, 0.5f).endVertex();
            buffer.pos(right, top, 0).tex(1, 1).endVertex();

            buffer.pos(right, top, 0).tex(1, 1).endVertex();
            if (progress < 0.375) { // Right.
                buffer.pos(midX, midY, 0).tex(0.5f, 0.5f).endVertex();

                final float y = Math.abs(ty / tx - 1) * 0.5f;
                buffer.pos(right, top + y * height, 0).tex(1, 1 - y).endVertex();
            } else {
                buffer.pos(midX, midY, 0).tex(0.5f, 0.5f).endVertex();
                buffer.pos(right, bottom, 0).tex(1, 0).endVertex();

                buffer.pos(right, bottom, 0).tex(1, 0).endVertex();
                if (progress < 0.625) { // Bottom.
                    buffer.pos(midX, midY, 0).tex(0.5f, 0.5f).endVertex();

                    final float x = Math.abs(tx / ty - 1) * 0.5f;
                    buffer.pos(left + x * width, bottom, 0).tex(x, 0).endVertex();
                } else {
                    buffer.pos(midX, midY, 0).tex(0.5f, 0.5f).endVertex();
                    buffer.pos(left, bottom, 0).tex(0, 0).endVertex();

                    buffer.pos(left, bottom, 0).tex(0, 0).endVertex();
                    if (progress < 0.875) { // Left.
                        buffer.pos(midX, midY, 0).tex(0.5f, 0.5f).endVertex();

                        final float y = (ty / tx + 1) * 0.5f;
                        buffer.pos(left, top + y * height, 0).tex(0, 1 - y).endVertex();
                    } else {
                        buffer.pos(midX, midY, 0).tex(0.5f, 0.5f).endVertex();
                        buffer.pos(left, top, 0).tex(0, 1).endVertex();

                        buffer.pos(left, top, 0).tex(0, 1).endVertex();
                        if (progress < 1) { // Top left.
                            buffer.pos(midX, midY, 0).tex(0.5f, 0.5f).endVertex();

                            final float x = Math.abs(tx / ty) * 0.5f;
                            buffer.pos(midX - x * width, top, 0).tex(0.5f - x, 1).endVertex();
                        } else {
                            buffer.pos(midX, midY, 0).tex(0.5f, 0.5f).endVertex();
                            buffer.pos(midX, top, 0).tex(0.5f, 1).endVertex();
                        }
                    }
                }
            }
        }

        tessellator.draw();

        Migration.FontRenderer.drawStringWithShadow(mc.fontRenderer, event.getMatrixStack(),
                new TranslationTextComponent(Constants.GUI_SCANNER_PROGRESS, MathHelper.floor(progress * 100)),
                right + 12, midY - mc.fontRenderer.FONT_HEIGHT / 2, 0xCCAACCEE);

        RenderSystem.bindTexture(0);
    }
}
