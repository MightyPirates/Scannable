package li.cil.scannable.common.item;

import net.minecraft.item.Item;

public abstract class AbstractItemScannerModule extends Item {
    AbstractItemScannerModule() {
        setMaxStackSize(1);
    }
}
