package li.cil.scannable.api.scanning;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.Optional;

/**
 * Implement this interface on an {@link Item} to have the mod treat it as a scanner module.
 */
public interface ScannerModuleProvider extends ItemLike {
    /**
     * Return the module implementation associated with this scanner module.
     * <p/>
     * The passed {@code module} should be of this {@link Item} instance.
     *
     * @param module the item stack representing a module.
     * @return the module implementation of this item.
     */
    Optional<ScannerModule> getScannerModule(final ItemStack module);
}
