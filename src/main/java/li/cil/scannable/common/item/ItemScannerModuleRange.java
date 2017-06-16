package li.cil.scannable.common.item;

import li.cil.scannable.common.config.Constants;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public final class ItemScannerModuleRange extends AbstractItemScannerModule {
    @Override
    public void addInformation(final ItemStack stack, @Nullable final World world, final List<String> tooltip, final ITooltipFlag flag) {
        tooltip.add(I18n.format(Constants.TOOLTIP_MODULE_RANGE));
        super.addInformation(stack, world, tooltip, flag);
    }
}
