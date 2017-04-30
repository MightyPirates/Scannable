package li.cil.scannable.common.init;

import li.cil.scannable.common.ProxyCommon;
import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.item.ItemScanner;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

public final class Items {
    public static Item scanner;

    public static void register(final ProxyCommon proxy) {
        scanner = proxy.registerItem(Constants.NAME_SCANNER, ItemScanner::new);
    }

    public static void addRecipes() {
        GameRegistry.addRecipe(new ShapedOreRecipe(
                new ItemStack(scanner, 1),
                "BIB",
                "SQS",
                "GRG",
                'B', Blocks.IRON_BARS,
                'I', "ingotIron",
                'S', "stickWood",
                'Q', "gemQuartz",
                'G', "ingotGold",
                'R', "dustRedstone"));
    }

    private Items() {
    }
}
