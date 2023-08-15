package li.cil.scannable.common.config;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import static net.minecraft.ChatFormatting.*;

public final class Strings {
    private static final String TOOLTIP_LIST_ITEM_FORMAT = "tooltip.scannable.list_item";

    public static final Component CREATIVE_TAB_TITLE = Component.translatable("itemGroup.scannable.common");

    public static final Component TOOLTIP_BLOCKS_LIST_CAPTION = Component.translatable("item.scannable.block_module.list").withStyle(GRAY);
    public static final Component TOOLTIP_ENTITIES_LIST_CAPTION = Component.translatable("item.scannable.entity_module.list").withStyle(GRAY);

    public static final Component GUI_BLOCKS_LIST_CAPTION = Component.translatable("gui.scannable.block_module.list");
    public static final Component GUI_ENTITIES_LIST_CAPTION = Component.translatable("gui.scannable.entity_module.list");

    public static final Component MESSAGE_NO_SCAN_MODULES = Component.translatable("message.scannable.no_scan_modules").withStyle(RED);
    public static final Component MESSAGE_NOT_ENOUGH_ENERGY = Component.translatable("message.scannable.not_enough_energy").withStyle(RED);
    public static final Component MESSAGE_NO_FREE_SLOTS = Component.translatable("message.scannable.no_free_slots").withStyle(RED);
    public static final Component MESSAGE_BLOCK_IGNORED = Component.translatable("message.scannable.block_ignored").withStyle(RED);

    public static Component listItem(final Component value) {
        return Component.translatable(Strings.TOOLTIP_LIST_ITEM_FORMAT, value);
    }

    public static Component energyStorage(final long stored, final long capacity) {
        final MutableComponent storedText = Component.literal(String.valueOf(stored)).withStyle(GREEN);
        final MutableComponent capacityText = Component.literal(String.valueOf(capacity)).withStyle(GREEN);
        return Component.translatable("item.scannable.scanner.energy", storedText, capacityText).withStyle(GRAY);
    }

    public static Component energyUsage(final int value) {
        final MutableComponent energyText = Component.literal(String.valueOf(value)).withStyle(GREEN);
        return Component.translatable("tooltip.scannable.module.energy_cost", energyText).withStyle(GRAY);
    }

    public static Component progress(final int value) {
        return Component.translatable("gui.scannable.scanner.progress", value);
    }
}
