/* SPDX-License-Identifier: MIT */

package li.cil.scannable.common.item.fabric;

import li.cil.scannable.api.fabric.Lookups;
import li.cil.scannable.api.scanning.ScannerModule;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public final class ScannerModuleItemImpl {
    public static Optional<ScannerModule> getModule(final ItemStack stack) {
        return Optional.ofNullable(Lookups.SCANNER_MODULE.find(stack, null));
    }
}
