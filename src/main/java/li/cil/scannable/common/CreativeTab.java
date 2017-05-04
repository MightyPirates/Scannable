package li.cil.scannable.common;

import li.cil.scannable.api.API;
import li.cil.scannable.common.init.Items;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public final class CreativeTab extends CreativeTabs {
    CreativeTab() {
        super(API.MOD_ID);
    }

    @Override
    public Item getTabIconItem() {
        return Items.scanner;
    }
}
