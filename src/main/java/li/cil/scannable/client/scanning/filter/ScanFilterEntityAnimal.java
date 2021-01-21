package li.cil.scannable.client.scanning.filter;

import li.cil.scannable.api.scanning.ScanFilterEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public enum ScanFilterEntityAnimal implements ScanFilterEntity {
    INSTANCE;

    @Override
    public boolean matches(final Entity entity) {
        return !(entity instanceof PlayerEntity) &&
               entity.getClassification(false).getPeacefulCreature();
    }
}
