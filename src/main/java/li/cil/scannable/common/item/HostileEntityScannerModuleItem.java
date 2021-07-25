package li.cil.scannable.common.item;

import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.scanning.HostileEntityScannerModule;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public final class HostileEntityScannerModuleItem extends AbstractScannerModuleItem {
    public HostileEntityScannerModuleItem() {
        super(HostileEntityScannerModule.INSTANCE);
    }

    // --------------------------------------------------------------------- //
    // Item

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(final ItemStack stack, @Nullable final Level level, final List<Component> tooltip, final TooltipFlag flag) {
        tooltip.add(new TranslatableComponent(Constants.TOOLTIP_MODULE_MONSTER));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
