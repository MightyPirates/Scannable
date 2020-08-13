package li.cil.scannable.common.energy;

import li.cil.scannable.common.config.Settings;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.EnergyStorage;

public final class EnergyStorageScanner extends EnergyStorage implements INBTSerializable<IntNBT> {
    private static final String TAG_ENERGY = "energy";

    private final ItemStack container;

    public EnergyStorageScanner(final ItemStack container) {
        super(Settings.energyCapacityScanner);
        this.container = container;
    }

    public void updateFromNBT() {
        final CompoundNBT nbt = container.getTag();
        if (nbt != null && nbt.contains(TAG_ENERGY, net.minecraftforge.common.util.Constants.NBT.TAG_INT)) {
            deserializeNBT((IntNBT) nbt.get(TAG_ENERGY));
        }
    }

    // --------------------------------------------------------------------- //
    // IEnergyStorage

    @Override
    public int receiveEnergy(final int maxReceive, final boolean simulate) {
        if (!Settings.useEnergy) {
            return 0;
        }

        final int energyReceived = super.receiveEnergy(maxReceive, simulate);
        if (!simulate && energyReceived != 0) {
            container.setTagInfo(TAG_ENERGY, serializeNBT());
        }

        return energyReceived;
    }

    @Override
    public int extractEnergy(final int maxExtract, final boolean simulate) {
        if (!Settings.useEnergy) {
            return 0;
        }

        final int energyExtracted = super.extractEnergy(maxExtract, simulate);
        if (!simulate && energyExtracted != 0) {
            container.setTagInfo(TAG_ENERGY, serializeNBT());
        }

        return energyExtracted;
    }

    // --------------------------------------------------------------------- //
    // INBTSerializable

    @Override
    public IntNBT serializeNBT() {
        return IntNBT.valueOf(energy);
    }

    @Override
    public void deserializeNBT(final IntNBT nbt) {
        energy = nbt.getInt();
    }
}
