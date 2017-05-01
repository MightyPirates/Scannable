package li.cil.scannable.client.gui;

import li.cil.scannable.api.API;
import li.cil.scannable.common.container.ContainerScanner;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;

public class GuiScanner extends GuiContainer {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(API.MOD_ID, "textures/gui/container/scanner.png");

    // --------------------------------------------------------------------- //

    public GuiScanner(final ContainerScanner container) {
        super(container);
        ySize = 133;
        allowUserInput = false;
    }

    // --------------------------------------------------------------------- //

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
        final ContainerScanner container = (ContainerScanner) this.inventorySlots;
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
