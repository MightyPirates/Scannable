package li.cil.scannable.mixin.forge;

import li.cil.scannable.common.item.ScannerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.extensions.IForgeItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ScannerItem.class)
public abstract class MixinScannerItem implements IForgeItem {
    @Override
    public boolean shouldCauseReequipAnimation(final ItemStack oldStack, final ItemStack newStack, final boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem() || slotChanged;
    }
}
