package li.cil.scannable.common.config;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;

import static net.minecraft.ChatFormatting.*;

public final class Strings {
    private static final String TOOLTIP_LIST_ITEM_FORMAT = "tooltip.scannable.list_item";

    public static final Component TOOLTIP_BLOCKS_LIST_CAPTION = new TranslatableComponent("item.scannable.module_block.list").withStyle(GRAY);
    public static final Component TOOLTIP_ENTITIES_LIST_CAPTION = new TranslatableComponent("item.scannable.module_entity.list").withStyle(GRAY);
    public static final Component TOOLTIP_SHOW_EXPLORED = new TranslatableComponent("item.scannable.module_structure.show_explored");
    public static final Component TOOLTIP_HIDE_EXPLORED = new TranslatableComponent("item.scannable.module_structure.hide_explored");

    public static final Component GUI_BLOCKS_LIST_CAPTION = new TranslatableComponent("gui.scannable.module_block.list");
    public static final Component GUI_ENTITIES_LIST_CAPTION = new TranslatableComponent("gui.scannable.module_entity.list");

    public static final Component MESSAGE_NO_SCAN_MODULES = new TranslatableComponent("message.scannable.no_scan_modules").withStyle(RED);
    public static final Component MESSAGE_NOT_ENOUGH_ENERGY = new TranslatableComponent("message.scannable.not_enough_energy").withStyle(RED);
    public static final Component MESSAGE_NO_FREE_SLOTS = new TranslatableComponent("message.scannable.no_free_slots").withStyle(RED);
    public static final Component MESSAGE_BLOCK_IGNORED = new TranslatableComponent("message.scannable.block_ignored").withStyle(RED);

    public static Component listItem(final Component value) {
        return new TranslatableComponent(Strings.TOOLTIP_LIST_ITEM_FORMAT, value);
    }

    public static Component energyStorage(final int stored, final int capacity) {
        final MutableComponent storedText = new TextComponent(String.valueOf(stored)).withStyle(GREEN);
        final MutableComponent capacityText = new TextComponent(String.valueOf(capacity)).withStyle(GREEN);
        return new TranslatableComponent("item.scannable.scanner.energy", storedText, capacityText).withStyle(GRAY);
    }

    public static Component energyUsage(final int value) {
        final MutableComponent energyText = new TextComponent(String.valueOf(value)).withStyle(GREEN);
        return new TranslatableComponent("tooltip.scannable.module.energy_cost", energyText).withStyle(GRAY);
    }

    public static Component progress(final int value) {
        return new TranslatableComponent("gui.scannable.scanner.progress", value);
    }

    public static Component withDistance(final Component caption, final float distance) {
        return new TranslatableComponent("gui.scannable.overlay.distance", caption, Mth.ceil(distance));
    }
}
