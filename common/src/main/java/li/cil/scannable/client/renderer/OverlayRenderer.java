package li.cil.scannable.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import li.cil.scannable.api.API;
import li.cil.scannable.client.shader.ScanPipelines;
import li.cil.scannable.common.config.Strings;
import li.cil.scannable.common.item.ScannerItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3x2f;

public final class OverlayRenderer {
    private static final Identifier PROGRESS = Identifier.fromNamespaceAndPath(API.MOD_ID, "textures/gui/overlay/scanner_progress.png");
    private static final int PROGRESS_SIZE = 64;
    private static final int PROGRESS_COLOR = ARGB.colorFromFloat(0.66f, 0.66f, 0.8f, 0.93f);

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

        final float progress = Mth.clamp(1 - (remaining - partialTick) / total, 0, 1);

        final int screenWidth = mc.getWindow().getGuiScaledWidth();
        final int screenHeight = mc.getWindow().getGuiScaledHeight();

        final int midX = screenWidth / 2;
        final int midY = screenHeight / 2;

        final var texture = mc.getTextureManager().getTexture(PROGRESS);
        graphics.guiRenderState.submitGuiElement(new ScannerProgressRenderState(
            ScanPipelines.SCANNER_PROGRESS,
            TextureSetup.singleTexture(texture.getTextureView(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST)),
            new Matrix3x2f(graphics.pose()),
            midX, midY,
            PROGRESS_SIZE,
            progress,
            PROGRESS_COLOR,
            null));

        final Component label = Strings.progress(Mth.floor(progress * 100));
        graphics.drawString(mc.font, label, midX + PROGRESS_SIZE / 2 + 12, midY - mc.font.lineHeight / 2, 0xCCAACCEE, true);
    }

    private OverlayRenderer() {
    }
}
