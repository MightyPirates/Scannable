package li.cil.scannable.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import li.cil.scannable.api.API;
import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.container.ContainerScanner;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class GuiScanner extends AbstractContainerScreen<ContainerScanner> {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(API.MOD_ID, "textures/gui/container/scanner.png");
    private static final TranslatableComponent SCANNER_MODULES_TEXT = new TranslatableComponent(Constants.GUI_SCANNER_MODULES);
    private static final TranslatableComponent SCANNER_MODULES_TOOLTIP = new TranslatableComponent(Constants.GUI_SCANNER_MODULES_TOOLTIP);
    private static final TranslatableComponent SCANNER_MODULES_INACTIVE_TEXT = new TranslatableComponent(Constants.GUI_SCANNER_MODULES_INACTIVE);
    private static final TranslatableComponent SCANNER_MODULES_INACTIVE_TOOLTIP = new TranslatableComponent(Constants.GUI_SCANNER_MODULES_INACTIVE_TOOLTIP);

    // --------------------------------------------------------------------- //

    public GuiScanner(final ContainerScanner container, final Inventory inventory, final Component title) {
        super(container, inventory, title);
        imageHeight = 159;
        passEvents = false;
        inventoryLabelX = 8;
        inventoryLabelY = 65;
    }

    // --------------------------------------------------------------------- //

    @Override
    public void render(final PoseStack matrixStack, final int mouseX, final int mouseY, final float partialTicks) {
        renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        if (isHovering(8, 23, font.width(SCANNER_MODULES_TEXT), font.lineHeight, mouseX, mouseY)) {
            renderTooltip(matrixStack, SCANNER_MODULES_TOOLTIP, mouseX, mouseY);
        }
        if (isHovering(8, 49, font.width(SCANNER_MODULES_INACTIVE_TEXT), font.lineHeight, mouseX, mouseY)) {
            renderTooltip(matrixStack, SCANNER_MODULES_INACTIVE_TOOLTIP, mouseX, mouseY);
        }

        renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(final PoseStack matrixStack, final int mouseX, final int mouseY) {
        super.renderLabels(matrixStack, mouseX, mouseY);

        font.draw(matrixStack, SCANNER_MODULES_TEXT, (float) 8, (float) 23, 0x404040);
        font.draw(matrixStack, SCANNER_MODULES_INACTIVE_TEXT, (float) 8, (float) 49, 0x404040);
    }

    @Override
    protected void renderBg(final PoseStack matrixStack, final float partialTicks, final int mouseX, final int mouseY) {
        RenderSystem.setShaderTexture(0, BACKGROUND);
        final int x = (width - imageWidth) / 2;
        final int y = (height - imageHeight) / 2;
        blit(matrixStack, x, y, 0, 0, imageWidth, imageHeight);
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

        super.slotClicked(slot, slotId, mouseButton, type);
    }
}
