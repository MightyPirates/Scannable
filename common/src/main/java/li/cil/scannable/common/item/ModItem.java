package li.cil.scannable.common.item;

import li.cil.scannable.util.TooltipUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class ModItem extends Item {
    protected ModItem(final Properties properties) {
        super(properties);
    }

    protected ModItem() {
        this(new Properties());
    }

    // --------------------------------------------------------------------- //

    @Environment(EnvType.CLIENT)
    @Override
    public void appendHoverText(final ItemStack stack, final Item.TooltipContext context, final List<Component> tooltip, final TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        TooltipUtils.tryAddDescription(stack, tooltip);
    }
}
