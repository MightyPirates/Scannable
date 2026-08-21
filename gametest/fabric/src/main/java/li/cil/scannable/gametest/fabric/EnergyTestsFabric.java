package li.cil.scannable.gametest.fabric;

import li.cil.scannable.gametest.EnergyTests;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyItem;

import java.util.function.Predicate;
import java.util.function.ToLongFunction;

import static li.cil.scannable.gametest.fabric.FabricTestSupport.TEMPLATE;

public final class EnergyTestsFabric {
    private static final ToLongFunction<ItemStack> RAW_ENERGY = SimpleEnergyItem::getStoredEnergyUnchecked;

    private static final Predicate<ItemStack> HAS_CAPABILITY = stack ->
        ContainerItemContext.withConstant(stack).find(EnergyStorage.ITEM) != null;

    @GameTest(template = TEMPLATE)
    public void freshScannerIsEmpty(final GameTestHelper helper) {
        EnergyTests.freshScannerIsEmpty(helper, RAW_ENERGY);
    }

    @GameTest(template = TEMPLATE)
    public void simulatedInsertDoesNotPersist(final GameTestHelper helper) {
        EnergyTests.simulatedInsertDoesNotPersist(helper, RAW_ENERGY);
    }

    @GameTest(template = TEMPLATE)
    public void insertPersistsToStack(final GameTestHelper helper) {
        EnergyTests.insertPersistsToStack(helper, RAW_ENERGY);
    }

    @GameTest(template = TEMPLATE)
    public void extractPersistsToStack(final GameTestHelper helper) {
        EnergyTests.extractPersistsToStack(helper, RAW_ENERGY);
    }

    @GameTest(template = TEMPLATE)
    public void insertClampsToCapacity(final GameTestHelper helper) {
        EnergyTests.insertClampsToCapacity(helper, RAW_ENERGY);
    }

    @GameTest(template = TEMPLATE)
    public void energySurvivesNewHandle(final GameTestHelper helper) {
        EnergyTests.energySurvivesNewHandle(helper, RAW_ENERGY);
    }

    @GameTest(template = TEMPLATE)
    public void noStorageWhenEnergyDisabled(final GameTestHelper helper) {
        EnergyTests.noStorageWhenEnergyDisabled(helper, RAW_ENERGY);
    }

    @GameTest(template = TEMPLATE)
    public void capabilityFollowsEnergyConfig(final GameTestHelper helper) {
        EnergyTests.capabilityFollowsEnergyConfig(helper, HAS_CAPABILITY);
    }
}
