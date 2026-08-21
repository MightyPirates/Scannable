/* SPDX-License-Identifier: MIT */

package li.cil.scannable.gametest.neoforge;

import li.cil.scannable.gametest.ModuleConfigurationTests;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import static li.cil.scannable.gametest.TestSupport.MOD_ID;
import static li.cil.scannable.gametest.TestSupport.TEMPLATE;

@GameTestHolder(MOD_ID)
@PrefixGameTestTemplate(false)
public final class ModuleConfigurationTestsNeoForge {
    @GameTest(template = TEMPLATE)
    public static void blockModuleRecordsUsedBlock(final GameTestHelper helper) {
        ModuleConfigurationTests.blockModuleRecordsUsedBlock(helper);
    }

    @GameTest(template = TEMPLATE)
    public static void blockModuleIgnoresDuplicates(final GameTestHelper helper) {
        ModuleConfigurationTests.blockModuleIgnoresDuplicates(helper);
    }

    @GameTest(template = TEMPLATE)
    public static void blockModuleStopsAtSlotLimit(final GameTestHelper helper) {
        ModuleConfigurationTests.blockModuleStopsAtSlotLimit(helper);
    }

    @GameTest(template = TEMPLATE)
    public static void blockModuleRejectsIgnoredBlock(final GameTestHelper helper) {
        ModuleConfigurationTests.blockModuleRejectsIgnoredBlock(helper);
    }

    @GameTest(template = TEMPLATE)
    public static void entityModuleRecordsInteractedEntity(final GameTestHelper helper) {
        ModuleConfigurationTests.entityModuleRecordsInteractedEntity(helper);
    }

    @GameTest(template = TEMPLATE)
    public static void entityModuleIgnoresDuplicates(final GameTestHelper helper) {
        ModuleConfigurationTests.entityModuleIgnoresDuplicates(helper);
    }

    private ModuleConfigurationTestsNeoForge() {
    }
}
