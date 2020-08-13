package li.cil.scannable.common.item;

import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.config.Settings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public abstract class AbstractItemScannerModule extends AbstractItem {
    AbstractItemScannerModule() {
        super(new Item.Properties().maxStackSize(1));
    }

    // --------------------------------------------------------------------- //
    // Item

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(final ItemStack stack, @Nullable final World world, final List<ITextComponent> tooltip, final ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);

        if (world == null) {
            return; // Presumably from initial search tree population where capabilities have not yet been initialized.
        }

        if (!Settings.useEnergy) {
            return;
        }

        if (stack.isEmpty()) {
            return;
        }

        final Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }

        final int cost = ItemScanner.getModuleEnergyCost(mc.player, stack);
        if (cost <= 0) {
            return;
        }

        tooltip.add(new TranslationTextComponent(Constants.TOOLTIP_MODULE_ENERGY_COST, cost));
    }
}
