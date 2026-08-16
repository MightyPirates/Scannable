package li.cil.scannable.gametest.neoforge;

import com.mojang.serialization.MapCodec;
import li.cil.scannable.common.item.ModDataComponents;
import li.cil.scannable.gametest.EnergyTests;
import li.cil.scannable.gametest.TestSupport;
import net.minecraft.core.Holder;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestInstance;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.transfer.access.ItemAccess;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.ToLongFunction;

@EventBusSubscriber(modid = TestSupport.MOD_ID)
public final class EnergyTestsNeoForge {
    // On NeoForge the energy value lives in the mod's own component.
    private static final ToLongFunction<ItemStack> RAW_ENERGY =
        stack -> stack.getOrDefault(ModDataComponents.ENERGY.get(), 0);

    private static final Predicate<ItemStack> HAS_CAPABILITY = stack ->
        ItemAccess.forStack(stack).getCapability(Capabilities.Energy.ITEM) != null;

    private static final Identifier EMPTY_STRUCTURE = Identifier.withDefaultNamespace("empty");
    private static final int MAX_TICKS = 20;

    // --------------------------------------------------------------------- //

    @SubscribeEvent
    public static void registerTests(final RegisterGameTestsEvent event) {
        final Holder<TestEnvironmentDefinition> environment =
            event.registerEnvironment(id("default"), new TestEnvironmentDefinition.AllOf(List.of()));

        register(event, environment, "fresh_scanner_is_empty", EnergyTests::freshScannerIsEmpty);
        register(event, environment, "simulated_insert_does_not_persist", EnergyTests::simulatedInsertDoesNotPersist);
        register(event, environment, "insert_persists_to_stack", EnergyTests::insertPersistsToStack);
        register(event, environment, "extract_persists_to_stack", EnergyTests::extractPersistsToStack);
        register(event, environment, "insert_clamps_to_capacity", EnergyTests::insertClampsToCapacity);
        register(event, environment, "energy_survives_new_handle", EnergyTests::energySurvivesNewHandle);
        register(event, environment, "no_storage_when_energy_disabled", EnergyTests::noStorageWhenEnergyDisabled);
        registerCapabilityTest(event, environment, "capability_follows_energy_config");
    }

    // --------------------------------------------------------------------- //

    private static void register(final RegisterGameTestsEvent event,
                                 final Holder<TestEnvironmentDefinition> environment,
                                 final String name,
                                 final EnergyTest test) {
        final TestData<Holder<TestEnvironmentDefinition>> data =
            new TestData<>(environment, EMPTY_STRUCTURE, MAX_TICKS, 0, true);
        event.registerTest(id(name), new DirectTestInstance(data, helper -> test.run(helper, RAW_ENERGY)));
    }

    private static void registerCapabilityTest(final RegisterGameTestsEvent event,
                                               final Holder<TestEnvironmentDefinition> environment,
                                               final String name) {
        final TestData<Holder<TestEnvironmentDefinition>> data =
            new TestData<>(environment, EMPTY_STRUCTURE, MAX_TICKS, 0, true);
        event.registerTest(id(name), new DirectTestInstance(data,
            helper -> EnergyTests.capabilityFollowsEnergyConfig(helper, HAS_CAPABILITY)));
    }

    private static Identifier id(final String path) {
        return Identifier.fromNamespaceAndPath(TestSupport.MOD_ID, path);
    }

    @FunctionalInterface
    private interface EnergyTest {
        void run(GameTestHelper helper, ToLongFunction<ItemStack> rawEnergy);
    }

    private static final class DirectTestInstance extends GameTestInstance {
        private final Consumer<GameTestHelper> test;

        DirectTestInstance(final TestData<Holder<TestEnvironmentDefinition>> info, final Consumer<GameTestHelper> test) {
            super(info);
            this.test = test;
        }

        @Override
        public void run(final GameTestHelper helper) {
            test.accept(helper);
        }

        @Override
        public MapCodec<? extends GameTestInstance> codec() {
            return MapCodec.unit(this);
        }

        @Override
        public MutableComponent typeDescription() {
            return Component.literal("scannable game test");
        }
    }

    // --------------------------------------------------------------------- //

    private EnergyTestsNeoForge() {
    }
}
