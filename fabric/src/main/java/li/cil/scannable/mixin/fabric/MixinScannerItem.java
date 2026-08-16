package li.cil.scannable.mixin.fabric;

import li.cil.scannable.common.item.ScannerItem;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ScannerItem.class)
public abstract class MixinScannerItem implements FabricItem {
    @Override
    public boolean allowComponentsUpdateAnimation(final Player player, final InteractionHand hand, final ItemStack oldStack, final ItemStack newStack) {
        return oldStack.getItem() != newStack.getItem();
    }
}
