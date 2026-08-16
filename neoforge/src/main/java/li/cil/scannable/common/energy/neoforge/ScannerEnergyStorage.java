package li.cil.scannable.common.energy.neoforge;

import li.cil.scannable.common.config.CommonConfig;
import li.cil.scannable.common.item.Items;
import li.cil.scannable.common.item.ModDataComponents;
import li.cil.scannable.common.item.ScannerItem;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public final class ScannerEnergyStorage extends SimpleEnergyHandler {
    private final ItemStack container;

    public ScannerEnergyStorage(final ItemStack container) {
        super(CommonConfig.energyCapacityScanner,
            CommonConfig.energyCapacityScanner,
            CommonConfig.energyCapacityScanner,
            Mth.clamp(container.getOrDefault(ModDataComponents.ENERGY.get(), 0), 0, CommonConfig.energyCapacityScanner));
        this.container = container;
    }

    public static ScannerEnergyStorage of(final ItemStack container) {
        if (container.getItem() instanceof ScannerItem) {
            return new ScannerEnergyStorage(container);
        } else {
            return new ScannerEnergyStorage(new ItemStack(Items.SCANNER.get()));
        }
    }

    // --------------------------------------------------------------------- //
    // SimpleEnergyHandler

    @Override
    public int insert(final int amount, final TransactionContext transaction) {
        if (!CommonConfig.useEnergy) {
            return 0;
        }

        return super.insert(amount, transaction);
    }

    @Override
    public int extract(final int amount, final TransactionContext transaction) {
        if (!CommonConfig.useEnergy) {
            return 0;
        }

        return super.extract(amount, transaction);
    }

    @Override
    protected void onEnergyChanged(final int previousAmount) {
        container.set(ModDataComponents.ENERGY.get(), energy);
    }
}
