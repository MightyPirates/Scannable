package li.cil.scannable.common.init;

import li.cil.scannable.common.ProxyCommon;
import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.item.ItemScanner;
import li.cil.scannable.common.item.ItemScannerModuleAnimal;
import li.cil.scannable.common.item.ItemScannerModuleBlank;
import li.cil.scannable.common.item.ItemScannerModuleBlockConfigurable;
import li.cil.scannable.common.item.ItemScannerModuleBlockFluid;
import li.cil.scannable.common.item.ItemScannerModuleBlockOreCommon;
import li.cil.scannable.common.item.ItemScannerModuleBlockOreRare;
import li.cil.scannable.common.item.ItemScannerModuleEntity;
import li.cil.scannable.common.item.ItemScannerModuleMonster;
import li.cil.scannable.common.item.ItemScannerModuleRange;
import li.cil.scannable.common.item.ItemScannerModuleStructure;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

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
    public static Item moduleFluid;
    public static Item moduleEntity;

    // --------------------------------------------------------------------- //

    public static boolean isScanner(final ItemStack stack) {
        return isItem(stack, scanner);
    }

    public static boolean isModuleRange(final ItemStack stack) {
        return isItem(stack, moduleRange);
    }

    public static boolean isModuleAnimal(final ItemStack stack) {
        return isItem(stack, moduleAnimal);
    }

    public static boolean isModuleMonster(final ItemStack stack) {
        return isItem(stack, moduleMonster);
    }

    public static boolean isModuleOreCommon(final ItemStack stack) {
        return isItem(stack, moduleOreCommon);
    }

    public static boolean isModuleOreRare(final ItemStack stack) {
        return isItem(stack, moduleOreRare);
    }

    public static boolean isModuleBlock(final ItemStack stack) {
        return isItem(stack, moduleBlock);
    }

    public static boolean isModuleStructure(final ItemStack stack) {
        return isItem(stack, moduleStructure);
    }

    public static boolean isModuleFluid(final ItemStack stack) {
        return isItem(stack, moduleFluid);
    }

    public static boolean isModuleEntity(final ItemStack stack) {
        return isItem(stack, moduleEntity);
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
        moduleFluid = proxy.registerItem(Constants.NAME_MODULE_FLUID, ItemScannerModuleBlockFluid::new);
        moduleEntity = proxy.registerItem(Constants.NAME_MODULE_ENTITY, ItemScannerModuleEntity::new);
    }

    // --------------------------------------------------------------------- //

    private static boolean isItem(final ItemStack stack, final Item item) {
        return !stack.isEmpty() && stack.getItem() == item;
    }

    // --------------------------------------------------------------------- //

    private Items() {
    }
}
