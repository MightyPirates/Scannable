package li.cil.scannable.common.item;

import li.cil.scannable.common.ModCreativeTabs;
import net.minecraft.world.item.Item;

public abstract class AbstractItem extends Item {
    protected AbstractItem(final Properties properties) {
        super(properties.tab(ModCreativeTabs.COMMON));
    }

    protected AbstractItem() {
        this(new Properties());
    }
}
