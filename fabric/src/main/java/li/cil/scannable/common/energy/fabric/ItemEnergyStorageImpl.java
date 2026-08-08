package li.cil.scannable.common.energy.fabric;

import li.cil.scannable.common.energy.ItemEnergyStorage;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.world.item.ItemStack;
import team.reborn.energy.api.EnergyStorage;

import java.util.Optional;

public final class ItemEnergyStorageImpl {
    public static Optional<ItemEnergyStorage> of(final ItemStack container) {
        final ContainerItemContext context = ContainerItemContext.ofSingleSlot(new SingleStackStorage() {
            private ItemStack current = container;

            @Override
            protected ItemStack getStack() {
                return current;
            }

            @Override
            protected void setStack(final ItemStack stack) {
                current = stack;
            }

            @Override
            protected void onFinalCommit() {
                container.applyComponents(current.getComponentsPatch());
            }
        });

        return Optional.ofNullable(context.find(EnergyStorage.ITEM))
            .map(storage -> new ItemEnergyStorage() {
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
