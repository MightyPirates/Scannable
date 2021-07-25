package li.cil.scannable.client.scanning.filter;

import net.minecraft.world.entity.Entity;

import java.util.List;
import java.util.function.Predicate;

public record EntityListScanFilter(List<Predicate<Entity>> filters) implements Predicate<Entity> {
    @Override
    public boolean test(final Entity entity) {
        return filters.stream().anyMatch(f -> f.test(entity));
    }
}
