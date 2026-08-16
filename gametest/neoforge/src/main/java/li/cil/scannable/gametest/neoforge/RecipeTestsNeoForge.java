package li.cil.scannable.gametest.neoforge;

import li.cil.scannable.gametest.RecipeTests;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.GameTest;

@ForEachTest(groups = "recipes")
public final class RecipeTestsNeoForge {
    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Every recipe the mod ships can be crafted in a crafting table.")
    public static void everyRecipeCraftsInCraftingTable(final GameTestHelper helper) {
        RecipeTests.everyRecipeCraftsInCraftingTable(helper);
    }

    private RecipeTestsNeoForge() {
    }
}
