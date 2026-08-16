package li.cil.scannable.gametest.fabric;

import li.cil.scannable.gametest.RecipeTests;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

/**
 * NeoForge runs the shared suites, so tests on plain vanilla systems live there only.
 * Recipes run here as well because their ingredients are {@code c:} tags, and the two
 * loaders fill those from separately maintained sources. The names agree today, so this
 * is only insurance against one side drifting.
 */
public final class RecipeTestsFabric {
    @GameTest
    public void everyRecipeCraftsInCraftingTable(final GameTestHelper helper) {
        RecipeTests.everyRecipeCraftsInCraftingTable(helper);
    }
}
