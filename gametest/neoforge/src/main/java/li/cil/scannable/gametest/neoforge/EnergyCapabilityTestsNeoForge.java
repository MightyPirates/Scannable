/* SPDX-License-Identifier: MIT */

package li.cil.scannable.gametest.neoforge;

import li.cil.scannable.common.item.Items;
import li.cil.scannable.common.item.ModDataComponents;
import li.cil.scannable.gametest.TestSupport;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

import static li.cil.scannable.gametest.TestSupport.*;

@GameTestHolder(MOD_ID)
@PrefixGameTestTemplate(false)
public final class EnergyCapabilityTestsNeoForge {
    private static final int AMOUNT = 1000;
    private static final int SLOT = 0;

    // --------------------------------------------------------------------- //

    @GameTest(template = TEMPLATE)
    public static void chargingInContainerSlotPersists(final GameTestHelper helper) {
        TestSupport.withEnergy(true, () -> {
            final SimpleContainer container = new SimpleContainer(1);
            container.setItem(SLOT, new ItemStack(Items.SCANNER.get()));

            final int inserted = slotEnergy(helper, container).receiveEnergy(AMOUNT, false);

            assertEquals(helper, "inserted amount", AMOUNT, inserted);
            assertEquals(helper, "energy persisted into the container slot",
                AMOUNT, storedEnergy(container));

            helper.succeed();
        });
    }

    // --------------------------------------------------------------------- //

    private static IEnergyStorage slotEnergy(final GameTestHelper helper, final SimpleContainer container) {
        final IItemHandler handler = new InvWrapper(container);
        final IEnergyStorage energy = handler.getStackInSlot(SLOT).getCapability(Capabilities.EnergyStorage.ITEM);
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
