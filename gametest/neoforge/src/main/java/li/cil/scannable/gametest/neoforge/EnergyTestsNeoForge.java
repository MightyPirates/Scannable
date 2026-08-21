package li.cil.scannable.gametest.neoforge;

import li.cil.scannable.common.item.ModDataComponents;
import li.cil.scannable.gametest.EnergyTests;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.function.Predicate;
import java.util.function.ToLongFunction;

import static li.cil.scannable.gametest.TestSupport.MOD_ID;
import static li.cil.scannable.gametest.TestSupport.TEMPLATE;

@GameTestHolder(MOD_ID)
@PrefixGameTestTemplate(false)
public final class EnergyTestsNeoForge {
    private static final ToLongFunction<ItemStack> RAW_ENERGY =
        stack -> stack.getOrDefault(ModDataComponents.ENERGY.get(), 0);

    private static final Predicate<ItemStack> HAS_CAPABILITY = stack ->
        stack.getCapability(Capabilities.EnergyStorage.ITEM) != null;

    // --------------------------------------------------------------------- //

    @GameTest(template = TEMPLATE)
    public static void freshScannerIsEmpty(final GameTestHelper helper) {
        EnergyTests.freshScannerIsEmpty(helper, RAW_ENERGY);
    }

    @GameTest(template = TEMPLATE)
    public static void simulatedInsertDoesNotPersist(final GameTestHelper helper) {
        EnergyTests.simulatedInsertDoesNotPersist(helper, RAW_ENERGY);
    }

    @GameTest(template = TEMPLATE)
    public static void insertPersistsToStack(final GameTestHelper helper) {
        EnergyTests.insertPersistsToStack(helper, RAW_ENERGY);
    }

    @GameTest(template = TEMPLATE)
    public static void extractPersistsToStack(final GameTestHelper helper) {
        EnergyTests.extractPersistsToStack(helper, RAW_ENERGY);
    }

    @GameTest(template = TEMPLATE)
    public static void insertClampsToCapacity(final GameTestHelper helper) {
        EnergyTests.insertClampsToCapacity(helper, RAW_ENERGY);
    }

    @GameTest(template = TEMPLATE)
    public static void energySurvivesNewHandle(final GameTestHelper helper) {
        EnergyTests.energySurvivesNewHandle(helper, RAW_ENERGY);
    }

    @GameTest(template = TEMPLATE)
    public static void noStorageWhenEnergyDisabled(final GameTestHelper helper) {
        EnergyTests.noStorageWhenEnergyDisabled(helper, RAW_ENERGY);
    }

    @GameTest(template = TEMPLATE)
    public static void capabilityFollowsEnergyConfig(final GameTestHelper helper) {
        EnergyTests.capabilityFollowsEnergyConfig(helper, HAS_CAPABILITY);
    }

    // --------------------------------------------------------------------- //

    private EnergyTestsNeoForge() {
    }
}
