/* SPDX-License-Identifier: MIT */

package li.cil.scannable.gametest.fabric;

import li.cil.scannable.common.item.Items;
import li.cil.scannable.gametest.TestSupport;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyItem;

import static li.cil.scannable.gametest.TestSupport.assertEquals;
import static li.cil.scannable.gametest.TestSupport.failure;

public final class EnergyCapabilityTestsFabric {
    private static final long AMOUNT = 1000;
    private static final int SLOT = 0;

    @GameTest
    public void chargingInContainerSlotPersists(final GameTestHelper helper) {
        TestSupport.withEnergy(true, () -> {
            final SimpleContainer container = container();
            final EnergyStorage energy = slotEnergy(helper, container);

            final long inserted;
            try (Transaction transaction = Transaction.openOuter()) {
                inserted = energy.insert(AMOUNT, transaction);
                transaction.commit();
            }

            assertEquals(helper, "inserted amount", AMOUNT, inserted);
            assertEquals(helper, "energy persisted into the container slot",
                AMOUNT, storedEnergy(container));

            helper.succeed();
        });
    }

    @GameTest
    public void abortedChargeInContainerSlotIsRolledBack(final GameTestHelper helper) {
        TestSupport.withEnergy(true, () -> {
            final SimpleContainer container = container();
            final EnergyStorage energy = slotEnergy(helper, container);

            try (Transaction transaction = Transaction.openOuter()) {
                energy.insert(AMOUNT, transaction);
                // Deliberately no commit, so the transaction aborts on close.
            }

            assertEquals(helper, "aborted charge must not persist", 0, storedEnergy(container));

            helper.succeed();
        });
    }

    // --------------------------------------------------------------------- //

    private static SimpleContainer container() {
        final SimpleContainer container = new SimpleContainer(1);
        container.setItem(SLOT, new ItemStack(Items.SCANNER.get()));
        return container;
    }

    private static EnergyStorage slotEnergy(final GameTestHelper helper, final SimpleContainer container) {
        final ContainerItemContext context = ContainerItemContext.ofSingleSlot(
            InventoryStorage.of(container, null).getSlot(SLOT));
        final EnergyStorage energy = context.find(EnergyStorage.ITEM);
        if (energy == null) {
            throw failure(helper, "no energy storage exposed for a scanner in a container slot");
        }
        return energy;
    }

    private static long storedEnergy(final SimpleContainer container) {
        return SimpleEnergyItem.getStoredEnergyUnchecked(container.getItem(SLOT));
    }
}
