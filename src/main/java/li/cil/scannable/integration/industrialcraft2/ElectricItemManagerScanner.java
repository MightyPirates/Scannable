package li.cil.scannable.integration.industrialcraft2;

import ic2.api.item.ElectricItem;
import ic2.api.item.IBackupElectricItemManager;
import li.cil.scannable.common.init.Items;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;

public enum ElectricItemManagerScanner implements IBackupElectricItemManager {
    INSTANCE;

    public static void init() {
        ElectricItem.registerBackupManager(INSTANCE);
    }

    // --------------------------------------------------------------------- //
    // IBackupElectricItemManager

    @Override
    public boolean handles(final ItemStack stack) {
        return Items.isScanner(stack);
    }

    // --------------------------------------------------------------------- //
    // IElectricItemManager

    @Override
    public double charge(final ItemStack stack, double amount, int tier, boolean ignoreTransferLimit, boolean simulate) {
        final IEnergyStorage energyStorage = stack.getCapability(CapabilityEnergy.ENERGY, null);
        if (energyStorage == null) {
            return 0;
        }
        if (!ignoreTransferLimit) {
            amount = Math.min(amount, 128);
        }
        return toIC2(energyStorage.receiveEnergy(fromIC2(amount), simulate));
    }

    @Override
    public double discharge(final ItemStack stack, double amount, int tier, boolean ignoreTransferLimit, boolean externally, boolean simulate) {
        final IEnergyStorage energyStorage = stack.getCapability(CapabilityEnergy.ENERGY, null);
        if (energyStorage == null) {
            return 0;
        }
        if (!ignoreTransferLimit) {
            amount = Math.min(amount, 128);
        }
        return toIC2(energyStorage.extractEnergy(fromIC2(amount), simulate));
    }

    @Override
    public double getCharge(final ItemStack stack) {
        final IEnergyStorage energyStorage = stack.getCapability(CapabilityEnergy.ENERGY, null);
        if (energyStorage == null) {
            return 0;
        }
        return toIC2(energyStorage.getEnergyStored());
    }

    @Override
    public double getMaxCharge(final ItemStack stack) {
        final IEnergyStorage energyStorage = stack.getCapability(CapabilityEnergy.ENERGY, null);
        if (energyStorage == null) {
            return 0;
        }
        return toIC2(energyStorage.getMaxEnergyStored());
    }

    @Override
    public boolean canUse(final ItemStack stack, final double amount) {
        return false;
    }

    @Override
    public boolean use(final ItemStack stack, final double amount, final EntityLivingBase entity) {
        return false;
    }

    @Override
    public void chargeFromArmor(final ItemStack stack, final EntityLivingBase entity) {
    }

    @Nullable
    @Override
    public String getToolTip(final ItemStack stack) {
        return null;
    }

    @Override
    public int getTier(final ItemStack stack) {
        return 1;
    }

    // --------------------------------------------------------------------- //

    private static int fromIC2(double value) {
        return (int) (value * 4);
    }

    private static double toIC2(int value) {
        return value * 0.25;
    }
}
