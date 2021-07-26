package li.cil.scannable.client.scanning.filter;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.function.Predicate;

@OnlyIn(Dist.CLIENT)
public record EntityListScanFilter(List<Predicate<Entity>> filters) implements Predicate<Entity> {
    @Override
    public boolean test(final Entity entity) {
        return filters.stream().anyMatch(f -> f.test(entity));
    }
}
