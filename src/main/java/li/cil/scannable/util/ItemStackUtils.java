package li.cil.scannable.util;

import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public final class ItemStackUtils {
    public static boolean isEmpty(@Nullable final ItemStack stack) {
        return stack == null || stack.getItem() == null || stack.stackSize <= 0;
    }

    private ItemStackUtils() {
    }
}
