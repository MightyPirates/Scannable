package li.cil.scannable.common.init;

import li.cil.scannable.api.API;
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
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

@GameRegistry.ObjectHolder(API.MOD_ID)
public final class Items {
    @GameRegistry.ObjectHolder(Constants.NAME_SCANNER)
    public static final Item scanner = null;
    @GameRegistry.ObjectHolder(Constants.NAME_MODULE_BLANK)
    public static final Item moduleBlank = null;
    @GameRegistry.ObjectHolder(Constants.NAME_MODULE_RANGE)
    public static final Item moduleRange = null;
    @GameRegistry.ObjectHolder(Constants.NAME_MODULE_ANIMAL)
    public static final Item moduleAnimal = null;
    @GameRegistry.ObjectHolder(Constants.NAME_MODULE_MONSTER)
    public static final Item moduleMonster = null;
    @GameRegistry.ObjectHolder(Constants.NAME_MODULE_ORE_COMMON)
    public static final Item moduleOreCommon = null;
    @GameRegistry.ObjectHolder(Constants.NAME_MODULE_ORE_RARE)
    public static final Item moduleOreRare = null;
    @GameRegistry.ObjectHolder(Constants.NAME_MODULE_BLOCK)
    public static final Item moduleBlock = null;
    @GameRegistry.ObjectHolder(Constants.NAME_MODULE_STRUCTURE)
    public static final Item moduleStructure = null;
    @GameRegistry.ObjectHolder(Constants.NAME_MODULE_FLUID)
    public static final Item moduleFluid = null;
    @GameRegistry.ObjectHolder(Constants.NAME_MODULE_ENTITY)
    public static final Item moduleEntity = null;

    public static List<Item> getAllItems() {
        return Arrays.asList(
                scanner,
                moduleBlank,
                moduleRange,
                moduleAnimal,
                moduleMonster,
                moduleOreCommon,
                moduleOreRare,
                moduleBlock,
                moduleStructure,
                moduleFluid,
                moduleEntity
        );
    }

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

    public static void register(final IForgeRegistry<Item> registry) {
        registerItem(registry, new ItemScanner(), Constants.NAME_SCANNER);
        registerItem(registry, new ItemScannerModuleBlank(), Constants.NAME_MODULE_BLANK);
        registerItem(registry, new ItemScannerModuleRange(), Constants.NAME_MODULE_RANGE);
        registerItem(registry, new ItemScannerModuleAnimal(), Constants.NAME_MODULE_ANIMAL);
        registerItem(registry, new ItemScannerModuleMonster(), Constants.NAME_MODULE_MONSTER);
        registerItem(registry, new ItemScannerModuleBlockOreCommon(), Constants.NAME_MODULE_ORE_COMMON);
        registerItem(registry, new ItemScannerModuleBlockOreRare(), Constants.NAME_MODULE_ORE_RARE);
        registerItem(registry, new ItemScannerModuleBlockConfigurable(), Constants.NAME_MODULE_BLOCK);
        registerItem(registry, new ItemScannerModuleStructure(), Constants.NAME_MODULE_STRUCTURE);
        registerItem(registry, new ItemScannerModuleBlockFluid(), Constants.NAME_MODULE_FLUID);
        registerItem(registry, new ItemScannerModuleEntity(), Constants.NAME_MODULE_ENTITY);
    }

    // --------------------------------------------------------------------- //

    private static void registerItem(final IForgeRegistry<Item> registry, final Item item, final String name) {
        registry.register(item.
                setUnlocalizedName(API.MOD_ID + "." + name).
                setCreativeTab(API.creativeTab).
                setRegistryName(name));
    }

    private static boolean isItem(final ItemStack stack, @Nullable final Item item) {
        return !stack.isEmpty() && stack.getItem() == item;
    }

    // --------------------------------------------------------------------- //

    private Items() {
    }
}
