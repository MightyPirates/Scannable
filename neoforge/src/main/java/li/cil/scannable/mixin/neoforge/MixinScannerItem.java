/* SPDX-License-Identifier: MIT */

package li.cil.scannable.mixin.neoforge;

import li.cil.scannable.common.item.ScannerItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.extensions.IItemExtension;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ScannerItem.class)
public abstract class MixinScannerItem implements IItemExtension {
    @Override
    public boolean shouldCauseReequipAnimation(final ItemStack oldStack, final ItemStack newStack, final boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem() || slotChanged;
    }
}
