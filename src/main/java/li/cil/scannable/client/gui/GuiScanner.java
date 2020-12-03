package li.cil.scannable.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import li.cil.scannable.api.API;
import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.container.ContainerScanner;
import li.cil.scannable.util.Migration;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;

public class GuiScanner extends ContainerScreen<ContainerScanner> {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(API.MOD_ID, "textures/gui/container/scanner.png");
    private static final TranslationTextComponent SCANNER_MODULES_TEXT = new TranslationTextComponent(Constants.GUI_SCANNER_MODULES);
    private static final TranslationTextComponent SCANNER_MODULES_TOOLTIP = new TranslationTextComponent(Constants.GUI_SCANNER_MODULES_TOOLTIP);
    private static final TranslationTextComponent SCANNER_MODULES_INACTIVE_TEXT = new TranslationTextComponent(Constants.GUI_SCANNER_MODULES_INACTIVE);
    private static final TranslationTextComponent SCANNER_MODULES_INACTIVE_TOOLTIP = new TranslationTextComponent(Constants.GUI_SCANNER_MODULES_INACTIVE_TOOLTIP);

    // --------------------------------------------------------------------- //

    private final ContainerScanner container;

    // --------------------------------------------------------------------- //

    public GuiScanner(final ContainerScanner container, final PlayerInventory inventory, final ITextComponent title) {
        super(container, inventory, title);
        this.container = container;
        ySize = 159;
        passEvents = false;
        playerInventoryTitleX = 8;
        playerInventoryTitleY = 65;
    }

    // --------------------------------------------------------------------- //

    @Override
    public void render(final MatrixStack matrixStack, final int mouseX, final int mouseY, final float partialTicks) {
        renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        if (isPointInRegion(8, 23, Migration.FontRenderer.getStringWidth(font, SCANNER_MODULES_TEXT), font.FONT_HEIGHT, mouseX, mouseY)) {
            renderTooltip(matrixStack, SCANNER_MODULES_TOOLTIP, mouseX, mouseY);
        }
        if (isPointInRegion(8, 49, Migration.FontRenderer.getStringWidth(font, SCANNER_MODULES_INACTIVE_TEXT), font.FONT_HEIGHT, mouseX, mouseY)) {
            renderTooltip(matrixStack, SCANNER_MODULES_INACTIVE_TOOLTIP, mouseX, mouseY);
        }

        renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(final MatrixStack matrixStack, final int mouseX, final int mouseY) {
        super.drawGuiContainerForegroundLayer(matrixStack, mouseX, mouseY);

        Migration.FontRenderer.drawString(font, matrixStack, SCANNER_MODULES_TEXT, 8, 23, 0x404040);
        Migration.FontRenderer.drawString(font, matrixStack, SCANNER_MODULES_INACTIVE_TEXT, 8, 49, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(final MatrixStack matrixStack, final float partialTicks, final int mouseX, final int mouseY) {
        RenderSystem.color4f(1, 1, 1, 1);
        minecraft.getTextureManager().bindTexture(BACKGROUND);
        final int x = (width - xSize) / 2;
        final int y = (height - ySize) / 2;
        blit(matrixStack, x, y, 0, 0, xSize, ySize);
    }

    @Override
    protected void handleMouseClick(@Nullable final Slot slot, final int slotId, final int mouseButton, final ClickType type) {
        if (slot != null) {
            final ItemStack scannerItemStack = playerInventory.player.getHeldItem(container.getHand());
            if (slot.getStack() == scannerItemStack) {
                return;
            }
            if (type == ClickType.SWAP && playerInventory.getStackInSlot(mouseButton) == scannerItemStack) {
                return;
            }
        }

        super.handleMouseClick(slot, slotId, mouseButton, type);
    }
}
