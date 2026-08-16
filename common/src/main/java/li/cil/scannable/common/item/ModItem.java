package li.cil.scannable.common.item;

import li.cil.scannable.util.TooltipUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class ModItem extends Item {
    protected ModItem(final Properties properties) {
        super(properties);
    }

    // --------------------------------------------------------------------- //

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(final ItemStack stack, final Item.TooltipContext context, final TooltipDisplay display, final Consumer<Component> tooltip, final TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltip, flag);
        TooltipUtils.tryAddDescription(stack, tooltip);
    }
}
