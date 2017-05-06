package li.cil.scannable.common.item;

import li.cil.scannable.common.config.Constants;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.List;

public final class ItemScannerModuleBlockOreCommon extends AbstractItemScannerModuleBlock {
    @Override
    public void addInformation(final ItemStack stack, final EntityPlayer player, final List<String> tooltip, final boolean advanced) {
        tooltip.add(I18n.format(Constants.TOOLTIP_MODULE_ORE_COMMON));
        super.addInformation(stack, player, tooltip, advanced);
    }
}
