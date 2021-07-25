package li.cil.scannable.data;

import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.LocationTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

import static li.cil.scannable.common.item.Items.*;

public final class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(final DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void buildCraftingRecipes(final Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(SCANNER.get())
                .pattern("i i")
                .pattern("brb")
                .pattern("gqg")
                .define('i', Tags.Items.INGOTS_IRON)
                .define('b', Items.IRON_BARS)
                .define('r', Tags.Items.DUSTS_REDSTONE)
                .define('g', Tags.Items.INGOTS_GOLD)
                .define('q', Tags.Items.GEMS_QUARTZ)
                .group("scanner")
                .unlockedBy("is_delving", LocationTrigger.TriggerInstance.located(LocationPredicate.inFeature(StructureFeature.MINESHAFT)))
                .save(consumer);

        ShapedRecipeBuilder.shaped(MODULE_BLANK.get())
                .pattern("ggg")
                .pattern("crc")
                .pattern("cnc")
                .define('g', Tags.Items.DYES_GREEN)
                .define('c', Items.CLAY_BALL)
                .define('r', Tags.Items.DUSTS_GLOWSTONE)
                .define('n', Tags.Items.NUGGETS_GOLD)
                .group("blank_module")
                .unlockedBy("has_scanner", InventoryChangeTrigger.TriggerInstance.hasItems(SCANNER.get()))
                .save(consumer);

        registerModule(MODULE_RANGE.get(), Tags.Items.ENDER_PEARLS).save(consumer);
        registerModule(MODULE_ENTITY.get(), Items.LEAD).save(consumer);
        registerModule(MODULE_ANIMAL.get(), Tags.Items.LEATHER).save(consumer);
        registerModule(MODULE_MONSTER.get(), Tags.Items.BONES).save(consumer);
        registerModule(MODULE_BLOCK.get(), Tags.Items.STONE).save(consumer);
        registerModule(MODULE_ORE_COMMON.get(), Items.COAL).save(consumer);
        registerModule(MODULE_ORE_RARE.get(), Tags.Items.GEMS_DIAMOND).save(consumer);
        registerModule(MODULE_FLUID.get(), Items.WATER_BUCKET).save(consumer);
//        registerModule(Scannable.MODULE_STRUCTURE.get(), Tags.Items.GEMS_EMERALD).build(consumer);
    }

    private static ShapelessRecipeBuilder registerModule(final Item item, final Tag<Item> ingredient) {
        return ShapelessRecipeBuilder.shapeless(item)
                .requires(MODULE_BLANK.get())
                .requires(ingredient)
                .group("scanner_module")
                .unlockedBy("has_blank_module", InventoryChangeTrigger.TriggerInstance.hasItems(MODULE_BLANK.get()));
    }

    private static ShapelessRecipeBuilder registerModule(final Item item, final Item ingredient) {
        return ShapelessRecipeBuilder.shapeless(item)
                .requires(MODULE_BLANK.get())
                .requires(ingredient)
                .group("scanner_module")
                .unlockedBy("has_blank_module", InventoryChangeTrigger.TriggerInstance.hasItems(MODULE_BLANK.get()));
    }
}
