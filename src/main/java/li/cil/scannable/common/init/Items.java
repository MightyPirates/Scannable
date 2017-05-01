package li.cil.scannable.common.init;

import li.cil.scannable.common.ProxyCommon;
import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.item.ItemScanner;
import li.cil.scannable.common.item.ItemScannerModuleAnimal;
import li.cil.scannable.common.item.ItemScannerModuleBlank;
import li.cil.scannable.common.item.ItemScannerModuleMonster;
import li.cil.scannable.common.item.ItemScannerModuleOreCommon;
import li.cil.scannable.common.item.ItemScannerModuleOreRare;
import li.cil.scannable.common.item.ItemScannerModuleRange;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

public final class Items {
    public static Item scanner;
    public static Item moduleBlank;
    public static Item moduleRange;
    public static Item moduleAnimal;
    public static Item moduleMonster;
    public static Item moduleOreCommon;
    public static Item moduleOreRare;

    // --------------------------------------------------------------------- //

    public static boolean isScanner(final ItemStack stack) {
        return isItem(stack, scanner);
    }

    // --------------------------------------------------------------------- //

    public static void register(final ProxyCommon proxy) {
        scanner = proxy.registerItem(Constants.NAME_SCANNER, ItemScanner::new);
        moduleBlank = proxy.registerItem(Constants.NAME_MODULE_BLANK, ItemScannerModuleBlank::new);
        moduleRange = proxy.registerItem(Constants.NAME_MODULE_RANGE, ItemScannerModuleRange::new);
        moduleAnimal = proxy.registerItem(Constants.NAME_MODULE_ANIMAL, ItemScannerModuleAnimal::new);
        moduleMonster = proxy.registerItem(Constants.NAME_MODULE_MONSTER, ItemScannerModuleMonster::new);
        moduleOreCommon = proxy.registerItem(Constants.NAME_MODULE_ORE_COMMON, ItemScannerModuleOreCommon::new);
        moduleOreRare = proxy.registerItem(Constants.NAME_MODULE_ORE_RARE, ItemScannerModuleOreRare::new);
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

    // --------------------------------------------------------------------- //

    private static boolean isItem(final ItemStack stack, final Item item) {
        return !stack.isEmpty() && stack.getItem() == item;
    }

    // --------------------------------------------------------------------- //

    private Items() {
    }
}
