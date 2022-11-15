package li.cil.scannable.common.energy.fabric;

import li.cil.scannable.common.energy.EnergyStorage;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

@SuppressWarnings("UnstableApiUsage")
public class EnergyStorageImpl {
    public static Optional<EnergyStorage> of(final ItemStack stack) {
        return Optional.ofNullable(ContainerItemContext.withInitial(stack).find(team.reborn.energy.api.EnergyStorage.ITEM))
            .map(storage -> new EnergyStorage() {
            @Override
            public long receiveEnergy(final long amount, final boolean simulate) {
                final long inserted;
                try (final Transaction transaction = Transaction.openOuter()) {
                    inserted = storage.insert(amount, transaction);
                    if (!simulate)
                        transaction.commit();
                }
                return inserted;
            }

            @Override
            public long extractEnergy(final long amount, final boolean simulate) {
                final long extracted;
                try (final Transaction transaction = Transaction.openOuter()) {
                    extracted = storage.extract(amount, transaction);
                    if (!simulate)
                        transaction.commit();
                }
                return extracted;
            }

            @Override
            public long getEnergyStored() {
                return storage.getAmount();
            }

            @Override
            public long getMaxEnergyStored() {
                return storage.getCapacity();
            }
        });
    }
}
