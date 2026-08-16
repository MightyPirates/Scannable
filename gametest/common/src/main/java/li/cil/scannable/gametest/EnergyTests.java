package li.cil.scannable.gametest;

import li.cil.scannable.common.energy.ItemEnergyStorage;
import li.cil.scannable.common.item.Items;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;
import java.util.function.ToLongFunction;

import static li.cil.scannable.gametest.TestSupport.assertEquals;
import static li.cil.scannable.gametest.TestSupport.assertTrue;
import static li.cil.scannable.gametest.TestSupport.failure;

public final class EnergyTests {
    private static final long AMOUNT = 1000;

    // --------------------------------------------------------------------- //

    public static void freshScannerIsEmpty(final GameTestHelper helper, final ToLongFunction<ItemStack> rawEnergy) {
        withEnergyEnabled(() -> {
            final ItemStack stack = scanner();
            final ItemEnergyStorage energy = storage(helper, stack);

            assertEquals(helper, "stored energy of a fresh scanner", 0, energy.getEnergyStored());
            assertEquals(helper, "persisted energy of a fresh scanner", 0, rawEnergy.applyAsLong(stack));
            assertTrue(helper, "capacity should be positive, was " + energy.getMaxEnergyStored(),
                energy.getMaxEnergyStored() > 0);

            helper.succeed();
        });
    }

    public static void simulatedInsertDoesNotPersist(final GameTestHelper helper, final ToLongFunction<ItemStack> rawEnergy) {
        withEnergyEnabled(() -> {
            final ItemStack stack = scanner();
            final ItemEnergyStorage energy = storage(helper, stack);

            final long accepted = energy.receiveEnergy(AMOUNT, true);

            assertEquals(helper, "simulated insert should report the accepted amount", AMOUNT, accepted);
            assertEquals(helper, "simulated insert must not change stored energy", 0, energy.getEnergyStored());
            assertEquals(helper, "simulated insert must not persist", 0, rawEnergy.applyAsLong(stack));

            helper.succeed();
        });
    }

    public static void insertPersistsToStack(final GameTestHelper helper, final ToLongFunction<ItemStack> rawEnergy) {
        withEnergyEnabled(() -> {
            final ItemStack stack = scanner();
            final ItemEnergyStorage energy = storage(helper, stack);

            final long accepted = energy.receiveEnergy(AMOUNT, false);

            assertEquals(helper, "insert should report the accepted amount", AMOUNT, accepted);
            assertEquals(helper, "insert should be visible through the handler", AMOUNT, energy.getEnergyStored());
            assertEquals(helper, "insert should be persisted on the stack", AMOUNT, rawEnergy.applyAsLong(stack));

            helper.succeed();
        });
    }

    public static void extractPersistsToStack(final GameTestHelper helper, final ToLongFunction<ItemStack> rawEnergy) {
        withEnergyEnabled(() -> {
            final ItemStack stack = scanner();
            final ItemEnergyStorage energy = storage(helper, stack);
            energy.receiveEnergy(AMOUNT, false);

            final long simulated = energy.extractEnergy(400, true);
            assertEquals(helper, "simulated extract should report the amount", 400, simulated);
            assertEquals(helper, "simulated extract must not persist", AMOUNT, rawEnergy.applyAsLong(stack));

            final long extracted = energy.extractEnergy(400, false);
            assertEquals(helper, "extract should report the amount", 400, extracted);
            assertEquals(helper, "extract should be visible through the handler", AMOUNT - 400, energy.getEnergyStored());
            assertEquals(helper, "extract should be persisted on the stack", AMOUNT - 400, rawEnergy.applyAsLong(stack));

            helper.succeed();
        });
    }

    public static void insertClampsToCapacity(final GameTestHelper helper, final ToLongFunction<ItemStack> rawEnergy) {
        withEnergyEnabled(() -> {
            final ItemStack stack = scanner();
            final ItemEnergyStorage energy = storage(helper, stack);
            final long capacity = energy.getMaxEnergyStored();

            final long accepted = energy.receiveEnergy(capacity * 2, false);

            assertEquals(helper, "insert beyond capacity should only accept what fits", capacity, accepted);
            assertEquals(helper, "stored energy should saturate at capacity", capacity, energy.getEnergyStored());
            assertEquals(helper, "persisted energy should saturate at capacity", capacity, rawEnergy.applyAsLong(stack));

            helper.succeed();
        });
    }

    public static void energySurvivesNewHandle(final GameTestHelper helper, final ToLongFunction<ItemStack> rawEnergy) {
        withEnergyEnabled(() -> {
            final ItemStack stack = scanner();
            storage(helper, stack).receiveEnergy(AMOUNT, false);

            assertEquals(helper, "a fresh handle should see the stored energy", AMOUNT, storage(helper, stack).getEnergyStored());
            assertEquals(helper, "the stack should carry the stored energy", AMOUNT, rawEnergy.applyAsLong(stack));

            helper.succeed();
        });
    }

    public static void noStorageWhenEnergyDisabled(final GameTestHelper helper, final ToLongFunction<ItemStack> rawEnergy) {
        withEnergy(false, () -> {
            assertTrue(helper, "a scanner should expose no energy storage while energy handling is disabled",
                ItemEnergyStorage.of(scanner()).isEmpty());

            helper.succeed();
        });
    }

    public static void capabilityFollowsEnergyConfig(final GameTestHelper helper, final Predicate<ItemStack> hasCapability) {
        withEnergy(true, () ->
            assertTrue(helper, "a scanner should expose the loader's energy capability while energy handling is enabled",
                hasCapability.test(scanner())));

        withEnergy(false, () ->
            assertTrue(helper, "a scanner should expose no energy capability while energy handling is disabled",
                !hasCapability.test(scanner())));

        helper.succeed();
    }

    // --------------------------------------------------------------------- //

    private static void withEnergyEnabled(final Runnable body) {
        withEnergy(true, body);
    }

    private static void withEnergy(final boolean enabled, final Runnable body) {
        TestSupport.withEnergy(enabled, body);
    }

    private static ItemStack scanner() {
        return new ItemStack(Items.SCANNER.get());
    }

    private static ItemEnergyStorage storage(final GameTestHelper helper, final ItemStack stack) {
        return ItemEnergyStorage.of(stack).orElseThrow(() ->
            failure(helper, "no energy storage exposed for " + stack));
    }

    // --------------------------------------------------------------------- //

    private EnergyTests() {
    }
}
