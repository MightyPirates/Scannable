package li.cil.scannable.client.scanning.filter;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Predicate;

@OnlyIn(Dist.CLIENT)
public enum HostileEntityScanFilter implements Predicate<Entity> {
    INSTANCE;

    @Override
    public boolean test(final Entity entity) {
        return entity instanceof LivingEntity &&
               !(entity instanceof Player) &&
               !entity.getClassification(false).isFriendly();
    }
}
