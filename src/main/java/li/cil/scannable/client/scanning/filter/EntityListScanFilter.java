package li.cil.scannable.client.scanning.filter;

import net.minecraft.world.entity.Entity;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;

import java.util.List;
import java.util.function.Predicate;

@Environment(EnvType.CLIENT)
public record EntityListScanFilter(List<Predicate<Entity>> filters) implements Predicate<Entity> {
    @Override
    public boolean test(final Entity entity) {
        return filters.stream().anyMatch(f -> f.test(entity));
    }
}
