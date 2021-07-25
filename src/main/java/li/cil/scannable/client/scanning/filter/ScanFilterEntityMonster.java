package li.cil.scannable.client.scanning.filter;

import li.cil.scannable.api.API;
import li.cil.scannable.api.scanning.ScanFilterEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public enum ScanFilterEntityMonster implements ScanFilterEntity {
    INSTANCE;

    @Override
    public boolean matches(final Entity entity) {
        return entity instanceof LivingEntity &&
               !(entity instanceof Player) &&
               !entity.getClassification(false).isFriendly();
    }

    @Override
    public Optional<ResourceLocation> getIcon(final Entity entity) {
        return Optional.of(API.ICON_WARNING);
    }
}
