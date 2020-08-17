package li.cil.scannable.client.scanning.filter;

import li.cil.scannable.api.scanning.ScanFilterEntity;
import net.minecraft.entity.Entity;

import java.util.List;

public final class ScanFilterEntityList implements ScanFilterEntity {
    private final List<ScanFilterEntity> filters;

    public ScanFilterEntityList(final List<ScanFilterEntity> filters) {
        this.filters = filters;
    }

    @Override
    public boolean matches(final Entity entity) {
        return filters.stream().anyMatch(f -> f.matches(entity));
    }
}
