package li.cil.scannable.api.scanning;

import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Optional;

/**
 * Specialized interface for using the built-in entity scan result provider.
 * <p>
 * When implementing this interface, return the result provider implementation
 * obtained from the provider registry like so:
 * <pre>
 * GameRegistry.findRegistry(ScanResultProvider.class).getValue(API.SCAN_RESULT_PROVIDER_ENTITIES);
 * </pre>
 */
public interface ScannerModuleEntity extends ScannerModule {
    /**
     * Get a filter that will be used for testing whether entities should be included
     * in the scan result.
     *
     * @param module the module to get the filter for.
     * @return the filter to use.
     */
    @OnlyIn(Dist.CLIENT)
    Optional<ScanFilterEntity> getFilter(final ItemStack module);
}
