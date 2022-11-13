package li.cil.scannable.common;

import li.cil.scannable.api.API;
import li.cil.scannable.common.item.Items;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public final class ModCreativeTabs {
    public static final CreativeModeTab COMMON = new CreativeModeTab(API.MOD_ID + ".common") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(Items.SCANNER.get());
        }
    };
}
