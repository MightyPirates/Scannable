package li.cil.scannable.client.gui;

import li.cil.scannable.api.API;
import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.container.ContainerScanner;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;

public class GuiScanner extends GuiContainer {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(API.MOD_ID, "textures/gui/container/scanner.png");

    // --------------------------------------------------------------------- //

    private final ContainerScanner container;

    // --------------------------------------------------------------------- //

    public GuiScanner(final ContainerScanner container) {
        super(container);
        this.container = container;
        ySize = 159;
        allowUserInput = false;
    }

    // --------------------------------------------------------------------- //

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (isPointInRegion(8, 23, fontRendererObj.getStringWidth(I18n.format(Constants.GUI_SCANNER_MODULES)), fontRendererObj.FONT_HEIGHT, mouseX, mouseY)) {
            drawCreativeTabHoveringText(I18n.format(Constants.GUI_SCANNER_MODULES_TOOLTIP), mouseX, mouseY);
        }
        if (isPointInRegion(8, 49, fontRendererObj.getStringWidth(I18n.format(Constants.GUI_SCANNER_MODULES_INACTIVE)), fontRendererObj.FONT_HEIGHT, mouseX, mouseY)) {
            drawCreativeTabHoveringText(I18n.format(Constants.GUI_SCANNER_MODULES_INACTIVE_TOOLTIP), mouseX, mouseY);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRendererObj.drawString(I18n.format(Constants.GUI_SCANNER_TITLE), 8, 6, 0x404040);
        fontRendererObj.drawString(I18n.format(Constants.GUI_SCANNER_MODULES), 8, 23, 0x404040);
        fontRendererObj.drawString(I18n.format(Constants.GUI_SCANNER_MODULES_INACTIVE), 8, 49, 0x404040);
        fontRendererObj.drawString(container.getPlayer().inventory.getDisplayName().getUnformattedText(), 8, 65, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(final float partialTicks, final int mouseX, final int mouseY) {
        GlStateManager.color(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(BACKGROUND);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
    }

    @Override
    protected void handleMouseClick(final Slot slot, final int slotId, final int mouseButton, final ClickType type) {
        final InventoryPlayer playerInventory = container.getPlayer().inventory;
        if (container.getHand() == EnumHand.MAIN_HAND && slot != null && slot.inventory == playerInventory) {
            final int currentItem = playerInventory.currentItem;
            if (slot.getSlotIndex() == currentItem) {
                return;
            }
            if (type == ClickType.SWAP && mouseButton == currentItem) {
                return;
            }
        }

        super.handleMouseClick(slot, slotId, mouseButton, type);
    }
}
