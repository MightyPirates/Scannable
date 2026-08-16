package li.cil.scannable.util;

import net.minecraft.ChatFormatting;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public final class TooltipUtils {
    public static void tryAddDescription(final ItemStack stack, final Consumer<Component> tooltip) {
        if (stack.isEmpty()) {
            return;
        }

        final String translationKey = stack.getItem().getDescriptionId() + ".desc";
        final Language language = Language.getInstance();
        if (language.has(translationKey)) {
            final MutableComponent description = Component.translatable(translationKey);
            tooltip.accept(description.withStyle(ChatFormatting.DARK_GRAY));
        }
    }
}
