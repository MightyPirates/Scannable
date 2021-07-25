package li.cil.scannable.common.item;

import li.cil.scannable.common.ModCreativeTabs;
import li.cil.scannable.util.TooltipUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class ModItem extends Item {
    protected ModItem(final Properties properties) {
        super(properties.tab(ModCreativeTabs.COMMON));
    }

    protected ModItem() {
        this(new Properties());
    }

    // --------------------------------------------------------------------- //

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(final ItemStack stack, @Nullable final Level level, final List<Component> tooltip, final TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        TooltipUtils.tryAddDescription(stack, tooltip);
    }
}
