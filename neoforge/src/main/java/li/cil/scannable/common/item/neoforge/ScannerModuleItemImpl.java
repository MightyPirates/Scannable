package li.cil.scannable.common.item.neoforge;

import li.cil.scannable.api.scanning.ScannerModule;
import li.cil.scannable.common.neoforge.capabilities.ModCapabilities;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public final class ScannerModuleItemImpl {
    public static Optional<ScannerModule> getModule(final ItemStack stack) {
        return Optional.ofNullable(stack.getCapability(ModCapabilities.ScannerModule.ITEM));
    }
}
