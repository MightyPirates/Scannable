package li.cil.scannable.common.energy.neoforge;

import li.cil.scannable.common.config.CommonConfig;
import li.cil.scannable.common.item.ModDataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;

public final class ScannerEnergyStorage extends SimpleEnergyHandler {
    private final ItemStack container;

    public ScannerEnergyStorage(final ItemStack container) {
        super(CommonConfig.energyCapacityScanner,
            CommonConfig.energyCapacityScanner,
            CommonConfig.energyCapacityScanner,
            Mth.clamp(container.getOrDefault(ModDataComponents.ENERGY.get(), 0), 0, CommonConfig.energyCapacityScanner));
        this.container = container;
    }

    // --------------------------------------------------------------------- //
    // SimpleEnergyHandler

    @Override
    protected void onEnergyChanged(final int previousAmount) {
        container.set(ModDataComponents.ENERGY.get(), energy);
    }
}
