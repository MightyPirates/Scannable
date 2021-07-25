package li.cil.scannable.common.item;

import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.scanning.ScannerModuleFluid;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public final class ItemScannerModuleBlockFluid extends AbstractItemScannerModule {
    public ItemScannerModuleBlockFluid() {
        super(ScannerModuleFluid.INSTANCE);
    }

    // --------------------------------------------------------------------- //
    // Item

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(final ItemStack stack, @Nullable final Level world, final List<Component> tooltip, final TooltipFlag flag) {
        tooltip.add(new TranslatableComponent(Constants.TOOLTIP_MODULE_FLUID));
        super.appendHoverText(stack, world, tooltip, flag);
    }
}
