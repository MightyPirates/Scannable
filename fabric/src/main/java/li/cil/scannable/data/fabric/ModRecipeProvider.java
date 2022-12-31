package li.cil.scannable.data.fabric;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static li.cil.scannable.common.item.Items.*;

public final class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(final FabricDataOutput output, final CompletableFuture<HolderLookup.Provider> ignoredRegistries) {
        super(output);
    }

    @Override
    public void buildRecipes(final Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, SCANNER.get())
            .pattern("i i")
            .pattern("brb")
            .pattern("gqg")
            .define('i', CommonItemTags.IRON_INGOTS)
            .define('b', Items.IRON_BARS)
            .define('r', CommonItemTags.REDSTONE_DUSTS)
            .define('g', CommonItemTags.GOLD_INGOTS)
            .define('q', CommonItemTags.QUARTZ_GEMS)
            .group("scanner")
            .unlockedBy("is_delving", PlayerTrigger.TriggerInstance.located(LocationPredicate.inStructure(BuiltinStructures.MINESHAFT)))
            .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BLANK_MODULE.get())
            .pattern("ggg")
            .pattern("crc")
            .pattern("cnc")
            .define('g', CommonItemTags.GREEN_DYES)
            .define('c', Items.CLAY_BALL)
            .define('r', CommonItemTags.GLOWSTONE_DUSTS)
            .define('n', CommonItemTags.GOLD_NUGGETS)
            .group("blank_module")
            .unlockedBy("has_scanner", InventoryChangeTrigger.TriggerInstance.hasItems(SCANNER.get()))
            .save(consumer);

        registerModule(RANGE_MODULE.get(), CommonItemTags.ENDER_PEARLS).save(consumer);
        registerModule(ENTITY_MODULE.get(), Items.LEAD).save(consumer);
        registerModule(FRIENDLY_ENTITY_MODULE.get(), CommonItemTags.LEATHER).save(consumer);
        registerModule(HOSTILE_ENTITY_MODULE.get(), CommonItemTags.BONES).save(consumer);
        registerModule(BLOCK_MODULE.get(), CommonItemTags.STONE).save(consumer);
        registerModule(COMMON_ORES_MODULE.get(), Items.COAL).save(consumer);
        registerModule(RARE_ORES_MODULE.get(), CommonItemTags.DIAMOND_GEMS).save(consumer);
        registerModule(FLUID_MODULE.get(), Items.WATER_BUCKET).save(consumer);
    }

    private static ShapelessRecipeBuilder registerModule(final Item item, final TagKey<Item> ingredient) {
        return ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, item)
            .requires(BLANK_MODULE.get())
            .requires(ingredient)
            .group("scanner_module")
            .unlockedBy("has_blank_module", InventoryChangeTrigger.TriggerInstance.hasItems(BLANK_MODULE.get()));
    }

    private static ShapelessRecipeBuilder registerModule(final Item item, final Item ingredient) {
        return ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, item)
            .requires(BLANK_MODULE.get())
            .requires(ingredient)
            .group("scanner_module")
            .unlockedBy("has_blank_module", InventoryChangeTrigger.TriggerInstance.hasItems(BLANK_MODULE.get()));
    }
}
