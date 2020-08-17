package li.cil.scannable.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import li.cil.scannable.api.API;
import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.container.ContainerScanner;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;

public class GuiScanner extends ContainerScreen<ContainerScanner> {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(API.MOD_ID, "textures/gui/container/scanner.png");

    // --------------------------------------------------------------------- //

    private final ContainerScanner container;

    // --------------------------------------------------------------------- //

    public GuiScanner(final ContainerScanner container, final PlayerInventory inventory, final ITextComponent title) {
        super(container, inventory, title);
        this.container = container;
        ySize = 159;
        passEvents = false;
    }

    // --------------------------------------------------------------------- //

    @Override
    public void render(final int mouseX, final int mouseY, final float partialTicks) {
        renderBackground();
        super.render(mouseX, mouseY, partialTicks);

        if (isPointInRegion(8, 23, font.getStringWidth(I18n.format(Constants.GUI_SCANNER_MODULES)), font.FONT_HEIGHT, mouseX, mouseY)) {
            renderTooltip(I18n.format(Constants.GUI_SCANNER_MODULES_TOOLTIP), mouseX, mouseY);
        }
        if (isPointInRegion(8, 49, font.getStringWidth(I18n.format(Constants.GUI_SCANNER_MODULES_INACTIVE)), font.FONT_HEIGHT, mouseX, mouseY)) {
            renderTooltip(I18n.format(Constants.GUI_SCANNER_MODULES_INACTIVE_TOOLTIP), mouseX, mouseY);
        }

        renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(final int mouseX, final int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        font.drawString(I18n.format(Constants.GUI_SCANNER_TITLE), 8, 6, 0x404040);
        font.drawString(I18n.format(Constants.GUI_SCANNER_MODULES), 8, 23, 0x404040);
        font.drawString(I18n.format(Constants.GUI_SCANNER_MODULES_INACTIVE), 8, 49, 0x404040);
        font.drawString(playerInventory.getDisplayName().getUnformattedComponentText(), 8, 65, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(final float partialTicks, final int mouseX, final int mouseY) {
        RenderSystem.color4f(1, 1, 1, 1);
        minecraft.getTextureManager().bindTexture(BACKGROUND);
        final int x = (width - xSize) / 2;
        final int y = (height - ySize) / 2;
        blit(x, y, 0, 0, xSize, ySize);
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
