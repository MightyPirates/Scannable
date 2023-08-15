package li.cil.scannable.client.gui;

import li.cil.scannable.api.API;
import li.cil.scannable.common.container.ScannerContainerMenu;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

@Environment(EnvType.CLIENT)
public class ScannerContainerScreen extends AbstractContainerScreen<ScannerContainerMenu> {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(API.MOD_ID, "textures/gui/container/scanner.png");
    private static final Component SCANNER_MODULES_TEXT = Component.translatable("gui.scannable.scanner.active_modules");
    private static final Component SCANNER_MODULES_TOOLTIP = Component.translatable("gui.scannable.scanner.active_modules.desc");
    private static final Component SCANNER_MODULES_INACTIVE_TEXT = Component.translatable("gui.scannable.scanner.inactive_modules");
    private static final Component SCANNER_MODULES_INACTIVE_TOOLTIP = Component.translatable("gui.scannable.scanner.inactive_modules.desc");

    // --------------------------------------------------------------------- //

    public ScannerContainerScreen(final ScannerContainerMenu container, final Inventory inventory, final Component title) {
        super(container, inventory, title);
        imageHeight = 159;
        inventoryLabelX = 8;
        inventoryLabelY = 65;
    }

    // --------------------------------------------------------------------- //

    @Override
    public void render(final GuiGraphics graphics, final int mouseX, final int mouseY, final float partialTicks) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);

        if (isHovering(8, 23, font.width(SCANNER_MODULES_TEXT), font.lineHeight, mouseX, mouseY)) {
            graphics.renderTooltip(font, SCANNER_MODULES_TOOLTIP, mouseX, mouseY);
        }
        if (isHovering(8, 49, font.width(SCANNER_MODULES_INACTIVE_TEXT), font.lineHeight, mouseX, mouseY)) {
            graphics.renderTooltip(font, SCANNER_MODULES_INACTIVE_TOOLTIP, mouseX, mouseY);
        }

        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(final GuiGraphics graphics, final int mouseX, final int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);

        graphics.drawString(font, SCANNER_MODULES_TEXT, 8, 23, 0x404040, false);
        graphics.drawString(font, SCANNER_MODULES_INACTIVE_TEXT, 8, 49, 0x404040, false);
    }

    @Override
    protected void renderBg(final GuiGraphics graphics, final float partialTicks, final int mouseX, final int mouseY) {
        final int x = (width - imageWidth) / 2;
        final int y = (height - imageHeight) / 2;
        graphics.blit(BACKGROUND, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void slotClicked(@Nullable final Slot slot, final int slotId, final int mouseButton, final ClickType type) {
        if (slot != null) {
            final ItemStack scannerItemStack = menu.getPlayer().getItemInHand(menu.getHand());
            if (slot.getItem() == scannerItemStack) {
                return;
            }
            if (type == ClickType.SWAP && menu.getPlayer().getInventory().getItem(mouseButton) == scannerItemStack) {
                return;
            }
        }

        //noinspection ConstantConditions Missing nullable annotation, base method tests for null.
        super.slotClicked(slot, slotId, mouseButton, type);
    }
}
