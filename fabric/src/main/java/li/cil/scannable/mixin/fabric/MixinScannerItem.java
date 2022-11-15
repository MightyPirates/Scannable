package li.cil.scannable.mixin.fabric;

import li.cil.scannable.common.config.CommonConfig;
import li.cil.scannable.common.item.ScannerItem;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import team.reborn.energy.api.base.SimpleBatteryItem;

@Mixin(ScannerItem.class)
public abstract class MixinScannerItem implements FabricItem, SimpleBatteryItem {
    // --------------------------------------------------------------------- //
    // FabricItem

    @Override
    public boolean allowNbtUpdateAnimation(final Player player, final InteractionHand hand, final ItemStack oldStack, final ItemStack newStack) {
        return oldStack.getItem() != newStack.getItem();
    }

    // --------------------------------------------------------------------- //
    // SimpleBatteryItem

    @Override
    public long getEnergyCapacity() {
        return CommonConfig.energyCapacityScanner;
    }

    @Override
    public long getEnergyMaxInput() {
        return Long.MAX_VALUE;
    }

    @Override
    public long getEnergyMaxOutput() {
        return Long.MAX_VALUE;
    }
}
