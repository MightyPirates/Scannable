package li.cil.scannable.common.scanning;

import li.cil.scannable.api.scanning.ScanResultProvider;
import li.cil.scannable.api.scanning.ScannerModule;
import li.cil.scannable.common.config.Settings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public enum ScannerModuleRange implements ScannerModule {
    INSTANCE;

    @Override
    public int getEnergyCost(final PlayerEntity player, final ItemStack module) {
        return Settings.energyCostModuleRange;
    }

    @Override
    public boolean hasResultProvider() {
        return false;
    }

    @Nullable
    @OnlyIn(Dist.CLIENT)
    @Override
    public ScanResultProvider getResultProvider() {
        return null;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float adjustGlobalRange(final float range) {
        return range + MathHelper.ceil(Settings.baseScanRadius / 2f);
    }
}
