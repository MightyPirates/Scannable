package li.cil.scannable.client.scanning.filter;

import li.cil.scannable.api.scanning.ScanFilterEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public enum ScanFilterEntityAnimal implements ScanFilterEntity {
    INSTANCE;

    @Override
    public boolean matches(final Entity entity) {
        return entity instanceof LivingEntity &&
               !(entity instanceof Player) &&
               entity.getClassification(false).isFriendly();
    }
}
