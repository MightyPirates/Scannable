package li.cil.scannable.common.container;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class ScannerSlot extends Slot {
    public ScannerSlot(final Container container, final int index, final int x, final int y) {
        super(container, index, x, y);
    }

    @Override
    public boolean mayPlace(final ItemStack itemStack) {
        return container.canPlaceItem(index, itemStack);
    }
}
