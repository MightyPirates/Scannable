package li.cil.scannable.common.item;

import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.config.Settings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public abstract class AbstractItemScannerModule extends Item {
    AbstractItemScannerModule() {
        setMaxStackSize(1);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(final ItemStack stack, @Nullable final World world, final List<String> tooltip, final ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);

        if (!Settings.useEnergy()) {
            return;
        }

        if (stack.isEmpty()) {
            return;
        }

        final Minecraft mc = Minecraft.getMinecraft();
        if (mc == null) {
            return;
        }

        final int cost = ItemScanner.getModuleEnergyCost(mc.player, stack);
        if (cost <= 0) {
            return;
        }

        tooltip.add(I18n.format(Constants.TOOLTIP_MODULE_ENERGY_COST, cost));
    }
}
