package li.cil.scannable.common.item;

import li.cil.scannable.common.capabilities.CapabilityProviderModule;
import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.scanning.ScannerModuleRange;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;
import java.util.List;

public final class ItemScannerModuleRange extends AbstractItemScannerModule {
    private static final ICapabilityProvider CAPABILITY_PROVIDER = new CapabilityProviderModule(ScannerModuleRange.INSTANCE);

    @Override
    public ICapabilityProvider initCapabilities(final ItemStack stack, @Nullable final CompoundNBT nbt) {
        return CAPABILITY_PROVIDER;
    }

    // --------------------------------------------------------------------- //
    // Item

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(final ItemStack stack, @Nullable final World world, final List<ITextComponent> tooltip, final ITooltipFlag flag) {
        tooltip.add(new TranslationTextComponent(Constants.TOOLTIP_MODULE_RANGE));
        super.appendHoverText(stack, world, tooltip, flag);
    }
}
