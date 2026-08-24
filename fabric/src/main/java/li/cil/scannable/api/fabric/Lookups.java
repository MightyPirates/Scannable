/* SPDX-License-Identifier: MIT */

package li.cil.scannable.api.fabric;

import li.cil.scannable.api.API;
import li.cil.scannable.api.scanning.ScannerModule;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.minecraft.resources.Identifier;

/**
 * The Fabric lookups Scannable provides.
 */
public final class Lookups {
    /**
     * Scanner modules, {@code scannable:scanner_module}.
     * <p>
     * Register an item with this to have Scannable treat it as a scanner module.
     */
    public static final ItemApiLookup<ScannerModule, Void> SCANNER_MODULE = ItemApiLookup.get(
        Identifier.fromNamespaceAndPath(API.MOD_ID, "scanner_module"), ScannerModule.class, Void.class);

    // --------------------------------------------------------------------- //

    private Lookups() {
    }
}
