package li.cil.scannable.gametest.fabric;

import li.cil.scannable.gametest.RecipeTests;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

import static li.cil.scannable.gametest.fabric.FabricTestSupport.TEMPLATE;

public final class RecipeTestsFabric {
    @GameTest(template = TEMPLATE)
    public void everyModItemIsCraftable(final GameTestHelper helper) {
        RecipeTests.everyModItemIsCraftable(helper);
    }

    @GameTest(template = TEMPLATE)
    public void everyRecipeCraftsInCraftingTable(final GameTestHelper helper) {
        RecipeTests.everyRecipeCraftsInCraftingTable(helper);
    }
}
