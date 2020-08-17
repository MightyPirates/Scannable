package li.cil.scannable.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import li.cil.scannable.api.API;
import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.container.AbstractModuleContainer;
import li.cil.scannable.common.network.Network;
import li.cil.scannable.common.network.message.MessageRemoveConfiguredModuleItemAt;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.util.List;

public abstract class AbstractConfigurableModuleScreen<TContainer extends AbstractModuleContainer, TItem> extends ContainerScreen<TContainer> {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(API.MOD_ID, "textures/gui/container/module_configurable.png");
    public static final int SLOTS_ORIGIN_X = 62;
    public static final int SLOTS_ORIGIN_Y = 20;
    public static final int SLOT_SIZE = 18;

    private final String listCaptionTranslationKey;

    // --------------------------------------------------------------------- //

    public AbstractConfigurableModuleScreen(final TContainer container, final PlayerInventory inventory, final ITextComponent title, final String listCaptionTranslationKey) {
        super(container, inventory, title);
        ySize = 133;
        passEvents = false;
        this.listCaptionTranslationKey = listCaptionTranslationKey;
    }

    private ItemStack getHeldItem() {
        return playerInventory.player.getHeldItem(container.getHand());
    }

    protected abstract List<TItem> getConfiguredItems(final ItemStack stack);

    protected abstract ITextComponent getItemName(final TItem item);

    protected abstract void renderConfiguredItem(final TItem item, final int x, final int y);

    protected void configureItemAt(final ItemStack stack, final int slot, final ItemStack value) {
    }

    // --------------------------------------------------------------------- //

    @Override
    public void render(final int mouseX, final int mouseY, final float partialTicks) {
        renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);

        final ItemStack stack = getHeldItem();
        final List<TItem> items = getConfiguredItems(stack);
        for (int slot = 0; slot < Math.min(items.size(), Constants.CONFIGURABLE_MODULE_SLOTS); slot++) {
            final int x = SLOTS_ORIGIN_X + slot * SLOT_SIZE;
            final int y = SLOTS_ORIGIN_Y;

            if (isPointInRegion(x, y, SLOT_SIZE, SLOT_SIZE, mouseX, mouseY)) {
                final TItem item = items.get(slot);
                renderTooltip(getItemName(item).getFormattedText(), mouseX, mouseY);
            }
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(final int mouseX, final int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        font.drawString(getHeldItem().getDisplayName().getFormattedText(), 8, 6, 0x404040);
        font.drawString(I18n.format(listCaptionTranslationKey), 8, 23, 0x404040);
        font.drawString(playerInventory.getDisplayName().getUnformattedComponentText(), 8, 39, 0x404040);

        final ItemStack stack = getHeldItem();
        final List<TItem> items = getConfiguredItems(stack);
        for (int slot = 0; slot < Constants.CONFIGURABLE_MODULE_SLOTS; slot++) {
            final int x = SLOTS_ORIGIN_X + slot * SLOT_SIZE;
            final int y = SLOTS_ORIGIN_Y;

            if (isPointInRegion(x, y, SLOT_SIZE, SLOT_SIZE, mouseX, mouseY)) {
                drawHoverHighlight(x, y, SLOT_SIZE - 2, SLOT_SIZE - 2);
            }

            if (slot < items.size()) {
                final TItem item = items.get(slot);
                renderConfiguredItem(item, x, y);
            }
        }
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
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        for (int slot = 0; slot < Constants.CONFIGURABLE_MODULE_SLOTS; slot++) {
            final int x = SLOTS_ORIGIN_X + slot * SLOT_SIZE;
            final int y = SLOTS_ORIGIN_Y;

            if (isPointInRegion(x, y, SLOT_SIZE, SLOT_SIZE, mouseX, mouseY)) {
                final ItemStack heldItemStack = playerInventory.getItemStack();
                if (!heldItemStack.isEmpty()) {
                    configureItemAt(getHeldItem(), slot, heldItemStack);
                } else {
                    Network.INSTANCE.sendToServer(new MessageRemoveConfiguredModuleItemAt(container.windowId, slot));
                }
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void handleMouseClick(@Nullable final Slot slot, final int slotId, final int mouseButton, final ClickType type) {
        if (slot != null) {
            final ItemStack heldItem = getHeldItem();
            if (slot.getStack() == heldItem) {
                return;
            }
            if (type == ClickType.SWAP && playerInventory.getStackInSlot(mouseButton) == heldItem) {
                return;
            }
        }

        super.handleMouseClick(slot, slotId, mouseButton, type);
    }

    // --------------------------------------------------------------------- //

    private void drawHoverHighlight(final int x, final int y, final int width, final int height) {
        RenderSystem.disableDepthTest();
        RenderSystem.colorMask(true, true, true, false);
        this.fillGradient(x, y, x + width, y + height, slotColor, slotColor);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.enableDepthTest();
    }
}
