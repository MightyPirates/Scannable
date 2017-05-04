package li.cil.scannable.client.renderer;

import li.cil.scannable.api.API;
import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.init.Items;
import li.cil.scannable.util.ItemStackUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public enum OverlayRenderer {
    INSTANCE;

    private static final ResourceLocation PROGRESS = new ResourceLocation(API.MOD_ID, "textures/gui/overlay/scanner_progress.png");

    @SubscribeEvent
    public void onOverlayRender(final RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        final Minecraft mc = Minecraft.getMinecraft();
        final EntityPlayer player = mc.player;

        final ItemStack stack = player.getActiveItemStack();
        if (ItemStackUtils.isEmpty(stack)) {
            return;
        }

        if (!Items.isScanner(stack)) {
            return;
        }

        final int total = stack.getMaxItemUseDuration();
        final int remaining = player.getItemInUseCount();

        final float progress = MathHelper.clamp(1 - (remaining - event.getPartialTicks()) / (float) total, 0, 1);

        final ScaledResolution resolution = event.getResolution();
        final int screenWidth = resolution.getScaledWidth();
        final int screenHeight = resolution.getScaledHeight();

        GlStateManager.enableBlend();
        GlStateManager.color(0.66f, 0.8f, 0.93f, 0.66f);
        mc.getTextureManager().bindTexture(PROGRESS);

        final Tessellator tessellator = Tessellator.getInstance();
        final VertexBuffer buffer = tessellator.getBuffer();

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

        buffer.pos(midX, top, 0).tex(0.5, 1).endVertex();
        if (progress < 0.125) { // Top right.
            buffer.pos(midX, midY, 0).tex(0.5, 0.5).endVertex();

            final float x = tx / ty * 0.5f;
            buffer.pos(midX + x * width, top, 0).tex(0.5 + x, 1).endVertex();
        } else {
            buffer.pos(midX, midY, 0).tex(0.5, 0.5).endVertex();
            buffer.pos(right, top, 0).tex(1, 1).endVertex();

            buffer.pos(right, top, 0).tex(1, 1).endVertex();
            if (progress < 0.375) { // Right.
                buffer.pos(midX, midY, 0).tex(0.5, 0.5).endVertex();

                final float y = Math.abs(ty / tx - 1) * 0.5f;
                buffer.pos(right, top + y * height, 0).tex(1, 1 - y).endVertex();
            } else {
                buffer.pos(midX, midY, 0).tex(0.5, 0.5).endVertex();
                buffer.pos(right, bottom, 0).tex(1, 0).endVertex();

                buffer.pos(right, bottom, 0).tex(1, 0).endVertex();
                if (progress < 0.625) { // Bottom.
                    buffer.pos(midX, midY, 0).tex(0.5, 0.5).endVertex();

                    final float x = Math.abs(tx / ty - 1) * 0.5f;
                    buffer.pos(left + x * width, bottom, 0).tex(x, 0).endVertex();
                } else {
                    buffer.pos(midX, midY, 0).tex(0.5, 0.5).endVertex();
                    buffer.pos(left, bottom, 0).tex(0, 0).endVertex();

                    buffer.pos(left, bottom, 0).tex(0, 0).endVertex();
                    if (progress < 0.875) { // Left.
                        buffer.pos(midX, midY, 0).tex(0.5, 0.5).endVertex();

                        final float y = (ty / tx + 1) * 0.5f;
                        buffer.pos(left, top + y * height, 0).tex(0, 1 - y).endVertex();
                    } else {
                        buffer.pos(midX, midY, 0).tex(0.5, 0.5).endVertex();
                        buffer.pos(left, top, 0).tex(0, 1).endVertex();

                        buffer.pos(left, top, 0).tex(0, 1).endVertex();
                        if (progress < 1) { // Top left.
                            buffer.pos(midX, midY, 0).tex(0.5, 0.5).endVertex();

                            final float x = Math.abs(tx / ty) * 0.5f;
                            buffer.pos(midX - x * width, top, 0).tex(0.5 - x, 1).endVertex();
                        } else {
                            buffer.pos(midX, midY, 0).tex(0.5, 0.5).endVertex();
                            buffer.pos(midX, top, 0).tex(0.5, 1).endVertex();
                        }
                    }
                }
            }
        }

        tessellator.draw();

        mc.fontRendererObj.drawString(I18n.format(Constants.GUI_SCANNER_PROGRESS, MathHelper.floor(progress * 100)), right + 12, midY - mc.fontRendererObj.FONT_HEIGHT / 2, 0xCCAACCEE, true);

        GlStateManager.bindTexture(0);
    }
}
