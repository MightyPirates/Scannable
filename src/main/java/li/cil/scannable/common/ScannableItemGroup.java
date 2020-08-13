package li.cil.scannable.common;

import li.cil.scannable.api.API;
import li.cil.scannable.common.item.ItemScanner;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public final class ScannableItemGroup extends ItemGroup {
    ScannableItemGroup() {
        super(API.MOD_ID);
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(ItemScanner.INSTANCE);
    }
}
