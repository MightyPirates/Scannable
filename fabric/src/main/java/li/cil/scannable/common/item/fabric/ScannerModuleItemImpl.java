package li.cil.scannable.common.item.fabric;

import li.cil.scannable.api.scanning.ScannerModule;
import li.cil.scannable.api.scanning.ScannerModuleProvider;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public final class ScannerModuleItemImpl {
    public static Optional<ScannerModule> getModule(final ItemStack stack) {
        if (stack.getItem() instanceof ScannerModuleProvider provider) {
            return provider.getScannerModule(stack);
        } else {
            return Optional.empty();
        }
    }
}
