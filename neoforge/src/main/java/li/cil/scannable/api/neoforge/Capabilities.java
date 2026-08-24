/* SPDX-License-Identifier: MIT */

package li.cil.scannable.api.neoforge;

import li.cil.scannable.api.API;
import li.cil.scannable.api.scanning.ScannerModule;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.capabilities.ItemCapability;

/**
 * The NeoForge capabilities Scannable provides.
 */
public final class Capabilities {
    /**
     * Scanner modules, {@code scannable:scanner_module}.
     * <p>
     * Register an item with this to have Scannable treat it as a scanner module.
     */
    public static final ItemCapability<ScannerModule, Void> SCANNER_MODULE = ItemCapability.createVoid(
        Identifier.fromNamespaceAndPath(API.MOD_ID, "scanner_module"), ScannerModule.class);

    // --------------------------------------------------------------------- //

    private Capabilities() {
    }
}
