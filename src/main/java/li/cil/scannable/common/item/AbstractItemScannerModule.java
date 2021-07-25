package li.cil.scannable.common.item;

import li.cil.scannable.api.scanning.ScannerModule;
import li.cil.scannable.common.capabilities.ScannerModuleWrapper;
import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.config.Settings;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;
import java.util.List;

public abstract class AbstractItemScannerModule extends AbstractItem {
    private final ICapabilityProvider capabilityProvider;

    // --------------------------------------------------------------------- //

    AbstractItemScannerModule(final ScannerModule module) {
        super(new Item.Properties().stacksTo(1));
        this.capabilityProvider = new ScannerModuleWrapper(module);
    }

    // --------------------------------------------------------------------- //

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(final ItemStack stack, @Nullable final CompoundTag nbt) {
        return capabilityProvider;
    }

    // --------------------------------------------------------------------- //
    // Item

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(final ItemStack stack, @Nullable final Level world, final List<Component> tooltip, final TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);

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

        tooltip.add(new TranslatableComponent(Constants.TOOLTIP_MODULE_ENERGY_COST, cost));
    }
}
