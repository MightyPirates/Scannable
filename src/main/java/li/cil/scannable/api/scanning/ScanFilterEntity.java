package li.cil.scannable.api.scanning;

import li.cil.scannable.api.API;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Optional;

/**
 * Filter provided by {@link ScannerModuleEntity} instances for the built-in entity {@link ScanResultProvider}.
 * <p>
 * Filters are queried for each potential candidate and should therefore be as efficient as possible. For this
 * reason they are their own object (instead of directly querying the module), so that they may perform some
 * internal caching.
 */
@OnlyIn(Dist.CLIENT)
public interface ScanFilterEntity {
    /**
     * Queried for each potential entity when performing a scan. Used to determine whether to include
     * the entity in the scan result or not.
     *
     * @param entity the entity to check.
     * @return <code>true</code> to have the entity included in the result; <code>false</code> otherwise.
     */
    boolean matches(final Entity entity);

    /**
     * The icon to display for the specified entity if it is included in a scan result.
     *
     * @param entity the entity to get the icon for.
     * @return the icon to use; if an {@link Optional#empty()} is returned {@link API#ICON_INFO} is used.
     */
    default Optional<ResourceLocation> getIcon(final Entity entity) {
        return Optional.of(API.ICON_INFO);
    }
}
