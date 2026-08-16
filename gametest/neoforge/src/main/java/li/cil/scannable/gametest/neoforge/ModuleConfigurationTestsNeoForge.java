package li.cil.scannable.gametest.neoforge;

import li.cil.scannable.gametest.ModuleConfigurationTests;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.GameTest;

@ForEachTest(groups = "module_configuration")
public final class ModuleConfigurationTestsNeoForge {
    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Using the block module on a block configures the module for it.")
    public static void blockModuleRecordsUsedBlock(final GameTestHelper helper) {
        ModuleConfigurationTests.blockModuleRecordsUsedBlock(helper);
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Using the block module on the same block twice configures it once.")
    public static void blockModuleIgnoresDuplicates(final GameTestHelper helper) {
        ModuleConfigurationTests.blockModuleIgnoresDuplicates(helper);
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "The block module stops accepting blocks once all slots are used.")
    public static void blockModuleStopsAtSlotLimit(final GameTestHelper helper) {
        ModuleConfigurationTests.blockModuleStopsAtSlotLimit(helper);
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "The block module refuses to be configured for an ignored block.")
    public static void blockModuleRejectsIgnoredBlock(final GameTestHelper helper) {
        ModuleConfigurationTests.blockModuleRejectsIgnoredBlock(helper);
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Interacting with an entity configures the entity module for its type.")
    public static void entityModuleRecordsInteractedEntity(final GameTestHelper helper) {
        ModuleConfigurationTests.entityModuleRecordsInteractedEntity(helper);
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Interacting with the same entity type twice configures it once.")
    public static void entityModuleIgnoresDuplicates(final GameTestHelper helper) {
        ModuleConfigurationTests.entityModuleIgnoresDuplicates(helper);
    }

    private ModuleConfigurationTestsNeoForge() {
    }
}
