package li.cil.scannable.common.energy.forge;

import li.cil.scannable.common.config.CommonConfig;
import li.cil.scannable.common.item.Items;
import li.cil.scannable.common.item.ScannerItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.EnergyStorage;

public final class ScannerEnergyStorage extends EnergyStorage {
    private static final String TAG_ENERGY = "energy";

    private final ItemStack container;

    public ScannerEnergyStorage(final ItemStack container) {
        super(CommonConfig.energyCapacityScanner);
        this.container = container;

        final CompoundTag tag = container.getTag();
        if (tag != null && tag.contains(TAG_ENERGY, Tag.TAG_INT)) {
            deserializeNBT(tag.get(TAG_ENERGY));
        }
    }

    public static ScannerEnergyStorage of(final ItemStack container) {
        if (container.getItem() instanceof ScannerItem) {
            return new ScannerEnergyStorage(container);
        } else {
            return new ScannerEnergyStorage(new ItemStack(Items.SCANNER.get()));
        }
    }

    // --------------------------------------------------------------------- //
    // IEnergyStorage

    @Override
    public int receiveEnergy(final int maxReceive, final boolean simulate) {
        if (!CommonConfig.useEnergy) {
            return 0;
        }

        final int energyReceived = super.receiveEnergy(maxReceive, simulate);
        if (!simulate && energyReceived != 0) {
            container.addTagElement(TAG_ENERGY, serializeNBT());
        }

        return energyReceived;
    }

    @Override
    public int extractEnergy(final int maxExtract, final boolean simulate) {
        if (!CommonConfig.useEnergy) {
            return 0;
        }

        final int energyExtracted = super.extractEnergy(maxExtract, simulate);
        if (!simulate && energyExtracted != 0) {
            container.addTagElement(TAG_ENERGY, serializeNBT());
        }

        return energyExtracted;
    }
}
