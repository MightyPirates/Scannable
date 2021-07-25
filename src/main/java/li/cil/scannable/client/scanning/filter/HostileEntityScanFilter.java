package li.cil.scannable.client.scanning.filter;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.function.Predicate;

public enum HostileEntityScanFilter implements Predicate<Entity> {
    INSTANCE;

    @Override
    public boolean test(final Entity entity) {
        return entity instanceof LivingEntity &&
               !(entity instanceof Player) &&
               !entity.getClassification(false).isFriendly();
    }
}
