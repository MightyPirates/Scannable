package li.cil.scannable.gametest.neoforge;

import li.cil.scannable.common.item.ModDataComponents;
import li.cil.scannable.gametest.EnergyTests;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.GameTest;

import java.util.function.Predicate;
import java.util.function.ToLongFunction;

@ForEachTest(groups = "energy")
public final class EnergyTestsNeoForge {
    // On NeoForge the energy value lives in the mod's own component.
    private static final ToLongFunction<ItemStack> RAW_ENERGY =
        stack -> stack.getOrDefault(ModDataComponents.ENERGY.get(), 0);

    private static final Predicate<ItemStack> HAS_CAPABILITY = stack ->
        ItemAccess.forStack(stack).getCapability(Capabilities.Energy.ITEM) != null;

    // --------------------------------------------------------------------- //

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "A fresh scanner reports no stored energy.")
    public static void freshScannerIsEmpty(final GameTestHelper helper) {
        EnergyTests.freshScannerIsEmpty(helper, RAW_ENERGY);
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Simulating an insert reports the amount without persisting it.")
    public static void simulatedInsertDoesNotPersist(final GameTestHelper helper) {
        EnergyTests.simulatedInsertDoesNotPersist(helper, RAW_ENERGY);
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Inserting energy persists it on the stack.")
    public static void insertPersistsToStack(final GameTestHelper helper) {
        EnergyTests.insertPersistsToStack(helper, RAW_ENERGY);
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Extracting energy persists the new amount on the stack.")
    public static void extractPersistsToStack(final GameTestHelper helper) {
        EnergyTests.extractPersistsToStack(helper, RAW_ENERGY);
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Inserting more than the capacity only accepts what fits.")
    public static void insertClampsToCapacity(final GameTestHelper helper) {
        EnergyTests.insertClampsToCapacity(helper, RAW_ENERGY);
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "A newly resolved storage sees energy written through an earlier one.")
    public static void energySurvivesNewHandle(final GameTestHelper helper) {
        EnergyTests.energySurvivesNewHandle(helper, RAW_ENERGY);
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "No energy storage is exposed while energy handling is disabled.")
    public static void noStorageWhenEnergyDisabled(final GameTestHelper helper) {
        EnergyTests.noStorageWhenEnergyDisabled(helper, RAW_ENERGY);
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "The energy capability follows the energy config setting.")
    public static void capabilityFollowsEnergyConfig(final GameTestHelper helper) {
        EnergyTests.capabilityFollowsEnergyConfig(helper, HAS_CAPABILITY);
    }

    private EnergyTestsNeoForge() {
    }
}
