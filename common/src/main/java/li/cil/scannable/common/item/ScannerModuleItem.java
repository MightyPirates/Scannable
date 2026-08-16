package li.cil.scannable.common.item;

import dev.architectury.injectables.annotations.ExpectPlatform;
import li.cil.scannable.api.scanning.ScannerModule;
import li.cil.scannable.common.config.CommonConfig;
import li.cil.scannable.common.config.Strings;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jetbrains.annotations.Contract;

import java.util.Optional;
import java.util.function.Consumer;

public class ScannerModuleItem extends ModItem {
    private final ScannerModule module;

    // --------------------------------------------------------------------- //

    ScannerModuleItem(final Item.Properties properties, final ScannerModule module) {
        super(properties.stacksTo(1));
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

    @Override
    public void appendHoverText(final ItemStack stack, final Item.TooltipContext context, final TooltipDisplay display, final Consumer<Component> tooltip, final TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltip, flag);

        final int cost = getModuleEnergyCost(stack);
        if (cost > 0) {
            tooltip.accept(Strings.energyUsage(cost));
        }
    }
}
