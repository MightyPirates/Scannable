package li.cil.scannable.client.gui;

import li.cil.scannable.api.API;
import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.container.AbstractModuleContainerMenu;
import li.cil.scannable.common.network.Network;
import li.cil.scannable.common.network.message.RemoveConfiguredModuleItemAtMessage;
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
import java.util.List;

@Environment(EnvType.CLIENT)
public abstract class AbstractConfigurableScannerModuleContainerScreen<TContainer extends AbstractModuleContainerMenu, TItem> extends AbstractContainerScreen<TContainer> {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(API.MOD_ID, "textures/gui/container/configurable_module.png");
    public static final int SLOTS_ORIGIN_X = 62;
    public static final int SLOTS_ORIGIN_Y = 20;
    public static final int SLOT_SIZE = 18;

    private final Component listCaption;
    private final Inventory inventory;

    // --------------------------------------------------------------------- //

    public AbstractConfigurableScannerModuleContainerScreen(final TContainer container, final Inventory inventory, final Component title, final Component listCaption) {
        super(container, inventory, title);
        this.listCaption = listCaption;
        this.inventory = inventory;

        imageHeight = 133;
        inventoryLabelX = 8;
        inventoryLabelY = 39;
    }

    private ItemStack getHeldItem() {
        return menu.getPlayer().getItemInHand(menu.getHand());
    }

    protected abstract List<TItem> getConfiguredItems(final ItemStack stack);

    protected abstract Component getItemName(final TItem item);

    protected abstract void renderConfiguredItem(final GuiGraphics graphics, final TItem item, final int x, final int y);

    protected void configureItemAt(final ItemStack stack, final int slot, final ItemStack value) {
    }

    // --------------------------------------------------------------------- //

    @Override
    public void render(final GuiGraphics graphics, final int mouseX, final int mouseY, final float partialTicks) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        renderTooltip(graphics, mouseX, mouseY);

        final ItemStack stack = getHeldItem();
        final List<TItem> items = getConfiguredItems(stack);
        for (int slot = 0; slot < Math.min(items.size(), Constants.CONFIGURABLE_MODULE_SLOTS); slot++) {
            final int x = SLOTS_ORIGIN_X + slot * SLOT_SIZE;
            final int y = SLOTS_ORIGIN_Y;

            if (isHovering(x, y, 16, 16, mouseX, mouseY)) {
                final TItem item = items.get(slot);
                graphics.renderTooltip(font, getItemName(item), mouseX, mouseY);
            }
        }
    }

    @Override
    protected void renderLabels(final GuiGraphics graphics, final int mouseX, final int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        graphics.drawString(font, listCaption, 8, 23, 0x404040, false);

        final ItemStack stack = getHeldItem();
        final List<TItem> items = getConfiguredItems(stack);
        for (int slot = 0; slot < Constants.CONFIGURABLE_MODULE_SLOTS; slot++) {
            final int x = SLOTS_ORIGIN_X + slot * SLOT_SIZE;
            final int y = SLOTS_ORIGIN_Y;

            if (isHovering(x, y, 16, 16, mouseX, mouseY)) {
                renderSlotHighlight(graphics, x, y, 400);
            }

            if (slot < items.size()) {
                final TItem item = items.get(slot);
                renderConfiguredItem(graphics, item, x, y);
            }
        }
    }

    @Override
    protected void renderBg(final GuiGraphics graphics, final float partialTicks, final int mouseX, final int mouseY) {
        final int x = (width - imageWidth) / 2;
        final int y = (height - imageHeight) / 2;
        graphics.blit(BACKGROUND, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        for (int slot = 0; slot < Constants.CONFIGURABLE_MODULE_SLOTS; slot++) {
            final int x = SLOTS_ORIGIN_X + slot * SLOT_SIZE;
            final int y = SLOTS_ORIGIN_Y;

            if (isHovering(x, y, SLOT_SIZE, SLOT_SIZE, mouseX, mouseY)) {
                final ItemStack heldItemStack = menu.getCarried();
                if (!heldItemStack.isEmpty()) {
                    configureItemAt(getHeldItem(), slot, heldItemStack);
                } else {
                    Network.sendToServer(new RemoveConfiguredModuleItemAtMessage(menu.containerId, slot));
                }
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void slotClicked(@Nullable final Slot slot, final int slotId, final int mouseButton, final ClickType type) {
        if (slot != null) {
            final ItemStack heldItem = getHeldItem();
            if (slot.getItem() == heldItem) {
                return;
            }
            if (type == ClickType.SWAP && inventory.getItem(mouseButton) == heldItem) {
                return;
            }
        }

        //noinspection ConstantConditions Missing nullable annotation, base method tests for null.
        super.slotClicked(slot, slotId, mouseButton, type);
    }
}
