package li.cil.scannable.common.item;

import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.config.Settings;
import li.cil.scannable.util.ItemStackUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

public abstract class AbstractItemScannerModule extends Item {
    AbstractItemScannerModule() {
        setMaxStackSize(1);
    }

    @Override
    public void addInformation(final ItemStack stack, final EntityPlayer player, final List<String> tooltip, final boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);

        if (!Settings.useEnergy()) {
            return;
        }

        if (ItemStackUtils.isEmpty(stack)) {
            return;
        }

        final int cost = ItemScanner.getModuleEnergyCost(player, stack);
        if (cost <= 0) {
            return;
        }

        tooltip.add(I18n.format(Constants.TOOLTIP_MODULE_ENERGY_COST, cost));
    }
}
