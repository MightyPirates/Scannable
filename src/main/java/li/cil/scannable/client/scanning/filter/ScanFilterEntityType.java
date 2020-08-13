package li.cil.scannable.client.scanning.filter;

import li.cil.scannable.api.scanning.ScanFilterEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

import java.util.Objects;

public final class ScanFilterEntityType implements ScanFilterEntity {
    private final EntityType<?> entityType;

    public ScanFilterEntityType(final EntityType<?> entityType) {
        this.entityType = entityType;
    }

    @Override
    public boolean matches(final Entity entity) {
        return Objects.equals(entityType, entity.getType());
    }
}
