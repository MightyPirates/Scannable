package li.cil.scannable.common.init;

import li.cil.scannable.common.ProxyCommon;
import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.item.*;
import li.cil.scannable.util.ItemStackUtils;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import javax.annotation.Nullable;

public final class Items {
    public static Item scanner;
    public static Item moduleBlank;
    public static Item moduleRange;
    public static Item moduleAnimal;
    public static Item moduleMonster;
    public static Item moduleOreCommon;
    public static Item moduleOreRare;
    public static Item moduleBlock;
    public static Item moduleStructure;

    // --------------------------------------------------------------------- //

    public static boolean isScanner(@Nullable final ItemStack stack) {
        return isItem(stack, scanner);
    }

    public static boolean isModuleRange(@Nullable final ItemStack stack) {
        return isItem(stack, moduleRange);
    }

    public static boolean isModuleAnimal(@Nullable final ItemStack stack) {
        return isItem(stack, moduleAnimal);
    }

    public static boolean isModuleMonster(@Nullable final ItemStack stack) {
        return isItem(stack, moduleMonster);
    }

    public static boolean isModuleOreCommon(@Nullable final ItemStack stack) {
        return isItem(stack, moduleOreCommon);
    }

    public static boolean isModuleOreRare(@Nullable final ItemStack stack) {
        return isItem(stack, moduleOreRare);
    }

    public static boolean isModuleBlock(@Nullable final ItemStack stack) {
        return isItem(stack, moduleBlock);
    }

    public static boolean isModuleStructure(final ItemStack stack) {
        return isItem(stack, moduleStructure);
    }

    // --------------------------------------------------------------------- //

    public static void register(final ProxyCommon proxy) {
        scanner = proxy.registerItem(Constants.NAME_SCANNER, ItemScanner::new);
        moduleBlank = proxy.registerItem(Constants.NAME_MODULE_BLANK, ItemScannerModuleBlank::new);
        moduleRange = proxy.registerItem(Constants.NAME_MODULE_RANGE, ItemScannerModuleRange::new);
        moduleAnimal = proxy.registerItem(Constants.NAME_MODULE_ANIMAL, ItemScannerModuleAnimal::new);
        moduleMonster = proxy.registerItem(Constants.NAME_MODULE_MONSTER, ItemScannerModuleMonster::new);
        moduleOreCommon = proxy.registerItem(Constants.NAME_MODULE_ORE_COMMON, ItemScannerModuleBlockOreCommon::new);
        moduleOreRare = proxy.registerItem(Constants.NAME_MODULE_ORE_RARE, ItemScannerModuleBlockOreRare::new);
        moduleBlock = proxy.registerItem(Constants.NAME_MODULE_BLOCK, ItemScannerModuleBlockConfigurable::new);
        moduleStructure = proxy.registerItem(Constants.NAME_MODULE_STRUCTURE, ItemScannerModuleStructure::new);
    }

    public static void addRecipes() {
        GameRegistry.addRecipe(new ShapedOreRecipe(
                new ItemStack(scanner),
                "I I",
                "BRB",
                "GQG",
                'B', Blocks.IRON_BARS,
                'I', "ingotIron",
                'Q', "gemQuartz",
                'G', "ingotGold",
                'R', "dustRedstone"));
        GameRegistry.addRecipe(new ShapedOreRecipe(
                new ItemStack(moduleBlank),
                "GGG",
                "CRC",
                "CNC",
                'G', "dyeGreen",
                'C', net.minecraft.init.Items.CLAY_BALL,
                'N', "nuggetGold",
                'R', "dustRedstone"));
        GameRegistry.addRecipe(new ShapelessOreRecipe(
                new ItemStack(moduleRange),
                moduleBlank, "enderpearl"));
        GameRegistry.addRecipe(new ShapelessOreRecipe(
                new ItemStack(moduleAnimal),
                moduleBlank, "leather"));
        GameRegistry.addRecipe(new ShapelessOreRecipe(
                new ItemStack(moduleMonster),
                moduleBlank, "bone"));
        GameRegistry.addRecipe(new ShapelessOreRecipe(
                new ItemStack(moduleOreCommon),
                moduleBlank, net.minecraft.init.Items.COAL));
        GameRegistry.addRecipe(new ShapelessOreRecipe(
                new ItemStack(moduleOreRare),
                moduleBlank, "gemDiamond"));
        GameRegistry.addRecipe(new ShapelessOreRecipe(
                new ItemStack(moduleBlock),
                moduleBlank, Blocks.STONE));
        GameRegistry.addRecipe(new ShapelessOreRecipe(
                new ItemStack(moduleStructure),
                moduleBlank, "gemEmerald"));
    }

    // --------------------------------------------------------------------- //

    private static boolean isItem(@Nullable final ItemStack stack, final Item item) {
        return !ItemStackUtils.isEmpty(stack) && stack.getItem() == item;
    }

    // --------------------------------------------------------------------- //

    private Items() {
    }
}
