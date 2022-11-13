package li.cil.scannable.common.item;

import li.cil.scannable.api.scanning.ScannerModule;
import li.cil.scannable.common.capabilities.ScannerModuleWrapper;
import li.cil.scannable.common.config.Strings;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;
import java.util.List;

public class ScannerModuleItem extends ModItem {
    private final ICapabilityProvider capabilityProvider;

    // --------------------------------------------------------------------- //

    ScannerModuleItem(final ScannerModule module) {
        super(new Item.Properties().stacksTo(1));
        this.capabilityProvider = new ScannerModuleWrapper(module);
    }

    // --------------------------------------------------------------------- //

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(final ItemStack stack, @Nullable final CompoundTag tag) {
        return capabilityProvider;
    }

    // --------------------------------------------------------------------- //
    // Item

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(final ItemStack stack, @Nullable final Level level, final List<Component> tooltip, final TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        final int cost = ScannerItem.getModuleEnergyCost(stack);
        if (cost > 0) {
            tooltip.add(Strings.energyUsage(cost));
        }
    }
}
