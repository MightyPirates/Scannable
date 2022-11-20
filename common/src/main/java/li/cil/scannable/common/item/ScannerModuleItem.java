package li.cil.scannable.common.item;

import dev.architectury.injectables.annotations.ExpectPlatform;
import li.cil.scannable.api.scanning.ScannerModule;
import li.cil.scannable.common.config.CommonConfig;
import li.cil.scannable.common.config.Strings;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class ScannerModuleItem extends ModItem {
    private final ScannerModule module;

    // --------------------------------------------------------------------- //

    ScannerModuleItem(final ScannerModule module) {
        super(new Item.Properties().stacksTo(1));
        this.module = module;
    }

    public ScannerModule getModule() {
        return module;
    }

    @SuppressWarnings("Contract")
    @Contract("_ -> !null")
    @ExpectPlatform
    public static Optional<ScannerModule> getModule(final ItemStack stack) {
        throw new AssertionError();
    }

    public static int getModuleEnergyCost(final ItemStack stack) {
        if (!CommonConfig.useEnergy) {
            return 0;
        }

        return getModule(stack)
            .map(module -> module.getEnergyCost(stack)).orElse(0);
    }

    // --------------------------------------------------------------------- //
    // Item

    @Environment(EnvType.CLIENT)
    @Override
    public void appendHoverText(final ItemStack stack, @Nullable final Level level, final List<Component> tooltip, final TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        final int cost = getModuleEnergyCost(stack);
        if (cost > 0) {
            tooltip.add(Strings.energyUsage(cost));
        }
    }
}
