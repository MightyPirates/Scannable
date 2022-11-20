package li.cil.scannable.common.item.forge;

import li.cil.scannable.api.scanning.ScannerModule;
import li.cil.scannable.common.forge.capabilities.Capabilities;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public final class ScannerModuleItemImpl {
    public static Optional<ScannerModule> getModule(final ItemStack stack) {
        if (Capabilities.SCANNER_MODULE_CAPABILITY == null) return Optional.empty();
        return stack.getCapability(Capabilities.SCANNER_MODULE_CAPABILITY).resolve();
    }
}
