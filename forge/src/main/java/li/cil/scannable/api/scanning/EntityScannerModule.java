package li.cil.scannable.api.scanning;

import li.cil.scannable.api.API;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * Specialized interface for using the built-in entity scan result provider.
 * <p>
 * When implementing this interface, return the result provider implementation
 * obtained from the provider registry like so:
 * <pre>
 * RegistryManager.ACTIVE.getRegistry(ScanResultProvider.class).getValue(API.SCAN_RESULT_PROVIDER_ENTITIES);
 * </pre>
 */
public interface EntityScannerModule extends ScannerModule {
    /**
     * The icon to display for the specified entity if it is included in a scan result.
     * <p>
     * Only called if the entity was matched by the filter returned by {@link #getFilter(ItemStack)}.
     *
     * @param entity the entity to get the icon for.
     * @return the icon to use; if an {@link Optional#empty()} is returned {@link API#ICON_INFO} is used.
     */
    @OnlyIn(Dist.CLIENT)
    default Optional<ResourceLocation> getIcon(final Entity entity) {
        return Optional.empty();
    }

    /**
     * Get a filter that will be used for testing whether entities should be included
     * in the scan result.
     *
     * @param module the module to get the filter for.
     * @return the filter to use.
     */
    @OnlyIn(Dist.CLIENT)
    Predicate<Entity> getFilter(final ItemStack module);
}
