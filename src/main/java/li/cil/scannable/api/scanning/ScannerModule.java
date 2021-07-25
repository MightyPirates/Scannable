package li.cil.scannable.api.scanning;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

/**
 * Interface for a scanner module.
 * <p>
 * Provide this as a capability in an item so that it can be installed in the
 * scanner. Once installed, it will be queried when the scanner is used.
 * <p>
 * Note that all of the scanning behavior is <em>client side only</em>. Only the
 * methods for energy cost ({@link #getEnergyCost(ItemStack)} and
 * for checking whether this module can be used on its own ({@link #hasResultProvider()})
 * will be called on the server.
 * <p>
 * For efficiency via batching, some modules may wish to reuse one of the built-in
 * {@link ScanResultProvider}s. This will usually require implementing a specialization
 * of this interface specific to that provider. For example, to use the built-in
 * module result provider (<code>scannable:blocks</code>) the {@link BlockScannerModule}
 * interface must be implemented.
 */
public interface ScannerModule {
    /**
     * Return the amount of energy this module adds to the total cost of a
     * scan when used by a {@link ScanResultProvider}.
     * <p>
     * Values should typically range between 25 and 100, for example, the
     * built-in animal provider requires 25 energy whereas the rare ore scanner
     * requires 100.
     *
     * @param module the module to get the energy cost for.
     * @return the energy cost contributed by this provider.
     */
    int getEnergyCost(final ItemStack module);

    /**
     * Whether this module has a result provider, i.e. can be used to perform scans
     * on its own.
     * <p>
     * This is used on the server side to determine whether a scan can be performed
     * with the currently equipped modules.
     *
     * @return <code>true</code> if {@link #getResultProvider()} is not <code>null</code>.
     */
    default boolean hasResultProvider() {
        return true;
    }

    /**
     * Get the {@link ScanResultProvider} this module uses.
     * <p>
     * This is the id of a provider in the scan provider registry. Built-in ones
     * include <code>scannable:blocks</code> and <code>scannable:entities</code>
     * and can be obtained from the {@link ScanResultProvider} registry, e.g.:
     * <pre>
     * RegistryManager.ACTIVE.getRegistry(ScanResultProvider.class).getValue(API.SCAN_RESULT_PROVIDER_BLOCKS);
     * </pre>
     * <p>
     * May return <code>null</code> if this module does not provide results on its
     * own. This can be the case for modules that modify other modules' behavior.
     *
     * @return the id of the scan provider this module uses.
     */
    @Nullable
    @OnlyIn(Dist.CLIENT)
    ScanResultProvider getResultProvider();

    /**
     * Modifies the global range of a scan. Modules can boost or reduce the range
     * for all other modules if present.
     * <p>
     * It is strongly recommended to only perform addition and subtraction here,
     * to avoid the order in which modules have been installed potentially
     * changing the final computed range.
     *
     * @param range the input range.
     * @return the adjusted range.
     */
    @OnlyIn(Dist.CLIENT)
    default float adjustGlobalRange(final float range) {
        return range;
    }
}
