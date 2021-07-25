package li.cil.scannable.client.scanning.filter;

import li.cil.scannable.api.scanning.ScanFilterEntity;
import net.minecraft.world.entity.Entity;

import java.util.List;

public record ScanFilterEntityList(List<ScanFilterEntity> filters) implements ScanFilterEntity {
    @Override
    public boolean matches(final Entity entity) {
        return filters.stream().anyMatch(f -> f.matches(entity));
    }
}
