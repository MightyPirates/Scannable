package li.cil.scannable.client.scanning.filter;

import li.cil.scannable.api.scanning.ScanFilterEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.Objects;

public record ScanFilterEntityType(EntityType<?> entityType) implements ScanFilterEntity {
    @Override
    public boolean matches(final Entity entity) {
        return Objects.equals(entityType, entity.getType());
    }
}
