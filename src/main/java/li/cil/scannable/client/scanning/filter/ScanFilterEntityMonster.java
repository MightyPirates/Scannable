package li.cil.scannable.client.scanning.filter;

import li.cil.scannable.api.API;
import li.cil.scannable.api.scanning.ScanFilterEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;

import java.util.Optional;

public enum ScanFilterEntityMonster implements ScanFilterEntity {
    INSTANCE;

    @Override
    public boolean matches(final Entity entity) {
        return !(entity instanceof PlayerEntity) &&
               !entity.getClassification(false).isFriendly();
    }

    @Override
    public Optional<ResourceLocation> getIcon(final Entity entity) {
        return Optional.of(API.ICON_WARNING);
    }
}
