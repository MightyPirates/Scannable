package li.cil.scannable.data;

import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
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
            .unlockedBy("is_delving", PlayerTrigger.TriggerInstance.located(LocationPredicate.inStructure(BuiltinStructures.MINESHAFT)))
            .save(consumer);

        ShapedRecipeBuilder.shaped(BLANK_MODULE.get())
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

        registerModule(RANGE_MODULE.get(), Tags.Items.ENDER_PEARLS).save(consumer);
        registerModule(ENTITY_MODULE.get(), Items.LEAD).save(consumer);
        registerModule(FRIENDLY_ENTITY_MODULE.get(), Tags.Items.LEATHER).save(consumer);
        registerModule(HOSTILE_ENTITY_MODULE.get(), Tags.Items.BONES).save(consumer);
        registerModule(BLOCK_MODULE.get(), Tags.Items.STONE).save(consumer);
        registerModule(COMMON_ORES_MODULE.get(), Items.COAL).save(consumer);
        registerModule(RARE_ORES_MODULE.get(), Tags.Items.GEMS_DIAMOND).save(consumer);
        registerModule(FLUID_MODULE.get(), Items.WATER_BUCKET).save(consumer);
    }

    private static ShapelessRecipeBuilder registerModule(final Item item, final TagKey<Item> ingredient) {
        return ShapelessRecipeBuilder.shapeless(item)
            .requires(BLANK_MODULE.get())
            .requires(ingredient)
            .group("scanner_module")
            .unlockedBy("has_blank_module", InventoryChangeTrigger.TriggerInstance.hasItems(BLANK_MODULE.get()));
    }

    private static ShapelessRecipeBuilder registerModule(final Item item, final Item ingredient) {
        return ShapelessRecipeBuilder.shapeless(item)
            .requires(BLANK_MODULE.get())
            .requires(ingredient)
            .group("scanner_module")
            .unlockedBy("has_blank_module", InventoryChangeTrigger.TriggerInstance.hasItems(BLANK_MODULE.get()));
    }
}
