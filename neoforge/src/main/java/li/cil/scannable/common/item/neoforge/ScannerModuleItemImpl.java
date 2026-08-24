/* SPDX-License-Identifier: MIT */

package li.cil.scannable.common.item.neoforge;

import li.cil.scannable.api.neoforge.Capabilities;
import li.cil.scannable.api.scanning.ScannerModule;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public final class ScannerModuleItemImpl {
    public static Optional<ScannerModule> getModule(final ItemStack stack) {
        return Optional.ofNullable(stack.getCapability(Capabilities.SCANNER_MODULE));
    }
}
