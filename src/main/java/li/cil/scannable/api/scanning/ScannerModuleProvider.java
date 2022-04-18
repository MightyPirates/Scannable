package li.cil.scannable.api.scanning;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Implement this interface on an {@link Item} to have the mod treat it as a scanner module.
 */
public interface ScannerModuleProvider {
    /**
     * Return the module implementation associated with this scanner module.
     *
     * @param module the module itself
     * @return the module implementation
     */
    ScannerModule getScannerModule(final ItemStack module);
}
