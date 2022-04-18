package li.cil.scannable.common.item;

import li.cil.scannable.api.scanning.ScannerModule;
import li.cil.scannable.api.scanning.ScannerModuleProvider;
import li.cil.scannable.common.config.Strings;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Simple item implementation for basic scanner modules.
 */
public class ScannerModuleItem extends ModItem implements ScannerModuleProvider {
    private final ScannerModule module;

    // --------------------------------------------------------------------- //

    ScannerModuleItem(final ScannerModule module) {
        super(new Item.Properties().stacksTo(1));
        this.module = module;
    }

    public ScannerModule getScannerModule(final ItemStack stack) {
        return module;
    }

    // --------------------------------------------------------------------- //
    // Item

    @Environment(EnvType.CLIENT)
    @Override
    public void appendHoverText(final ItemStack stack, @Nullable final Level level, final List<Component> tooltip, final TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        final int cost = ScannerItem.getModuleEnergyCost(stack);
        if (cost > 0) {
            tooltip.add(Strings.energyUsage(cost));
        }
    }
}
