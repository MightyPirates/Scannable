/* SPDX-License-Identifier: MIT */

package li.cil.scannable.gametest.neoforge;

import li.cil.scannable.common.config.ServerConfig;
import li.cil.scannable.common.item.Items;
import li.cil.scannable.common.item.ModDataComponents;
import li.cil.scannable.gametest.TestSupport;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.GameTest;

import static li.cil.scannable.gametest.TestSupport.assertEquals;
import static li.cil.scannable.gametest.TestSupport.assertTrue;
import static li.cil.scannable.gametest.TestSupport.failure;

@ForEachTest(groups = "energy_capability")
public final class EnergyCapabilityTestsNeoForge {
    private static final int AMOUNT = 1000;
    private static final int SLOT = 0;

    // --------------------------------------------------------------------- //

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Charging a scanner held in a container slot persists to that slot.")
    public static void chargingInContainerSlotPersists(final GameTestHelper helper) {
        TestSupport.withEnergy(true, () -> {
            final SimpleContainer container = new SimpleContainer(1);
            container.setItem(SLOT, new ItemStack(Items.SCANNER.get()));

            final EnergyHandler energy = slotEnergy(helper, container);
            final int inserted;
            try (Transaction transaction = Transaction.openRoot()) {
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
    @EmptyTemplate
    @TestHolder(description = "An aborted charge of a scanner in a container slot leaves it untouched.")
    public static void abortedChargeInContainerSlotIsRolledBack(final GameTestHelper helper) {
        TestSupport.withEnergy(true, () -> {
            final SimpleContainer container = new SimpleContainer(1);
            container.setItem(SLOT, new ItemStack(Items.SCANNER.get()));

            final EnergyHandler energy = slotEnergy(helper, container);
            try (Transaction transaction = Transaction.openRoot()) {
                energy.insert(AMOUNT, transaction);
                // Deliberately no commit, so the transaction aborts on close.
            }

            assertEquals(helper, "aborted charge must not persist", 0, storedEnergy(container));

            helper.succeed();
        });
    }

    // --------------------------------------------------------------------- //

    private static EnergyHandler slotEnergy(final GameTestHelper helper, final SimpleContainer container) {
        assertTrue(helper, "energy handling must be enabled for this test", ServerConfig.useEnergy);

        final ResourceHandler<ItemResource> handler = VanillaContainerWrapper.of(container);
        final ItemAccess access = ItemAccess.forHandlerIndex(handler, SLOT);
        final EnergyHandler energy = access.getCapability(Capabilities.Energy.ITEM);
        if (energy == null) {
            throw failure(helper, "no energy capability exposed for a scanner in a container slot");
        }
        return energy;
    }

    private static int storedEnergy(final SimpleContainer container) {
        return container.getItem(SLOT).getOrDefault(ModDataComponents.ENERGY.get(), 0);
    }

    private EnergyCapabilityTestsNeoForge() {
    }
}
