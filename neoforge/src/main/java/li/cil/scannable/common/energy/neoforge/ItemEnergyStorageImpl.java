package li.cil.scannable.common.energy.neoforge;

import li.cil.scannable.common.energy.ItemEnergyStorage;
import li.cil.scannable.common.item.ScannerItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.Optional;
import java.util.function.ToIntBiFunction;

public final class ItemEnergyStorageImpl {
    public static Optional<ItemEnergyStorage> of(final ItemStack container) {
        if (!(container.getItem() instanceof ScannerItem)) {
            return Optional.empty();
        }

        final EnergyHandler handler = new ScannerEnergyStorage(container);

        return Optional.of(new ItemEnergyStorage() {
            @Override
            public long receiveEnergy(final long amount, final boolean simulate) {
                return run(handler::insert, amount, simulate);
            }

            @Override
            public long extractEnergy(final long amount, final boolean simulate) {
                return run(handler::extract, amount, simulate);
            }

            @Override
            public long getEnergyStored() {
                return handler.getAmountAsLong();
            }

            @Override
            public long getMaxEnergyStored() {
                return handler.getCapacityAsLong();
            }
        });
    }

    private static long run(final ToIntBiFunction<Integer, TransactionContext> operation, final long amount, final boolean simulate) {
        final int clampedAmount = (int) Math.min(amount, Integer.MAX_VALUE);
        try (Transaction transaction = Transaction.openRoot()) {
            final int result = operation.applyAsInt(clampedAmount, transaction);
            if (!simulate) {
                transaction.commit();
            }
            return result;
        }
    }

    private ItemEnergyStorageImpl() {
    }
}
