package li.cil.scannable.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.LocationTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;

import java.util.function.Consumer;

import static li.cil.scannable.common.item.Items.*;

public final class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(final FabricDataGenerator generator) {
        super(generator);
    }

    @Override
    protected void generateRecipes(final Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(SCANNER.get())
                .pattern("i i")
                .pattern("brb")
                .pattern("gqg")
                .define('i', Items.IRON_INGOT)
                .define('b', Items.IRON_BARS)
                .define('r', Items.REDSTONE)
                .define('g', Items.GOLD_INGOT)
                .define('q', Items.QUARTZ)
                .group("scanner")
                .unlockedBy("is_delving", LocationTrigger.TriggerInstance.located(LocationPredicate.inFeature(BuiltinStructures.MINESHAFT)))
                .save(consumer);

        ShapedRecipeBuilder.shaped(BLANK_MODULE.get())
                .pattern("ggg")
                .pattern("crc")
                .pattern("cnc")
                .define('g', Items.GREEN_DYE)
                .define('c', Items.CLAY_BALL)
                .define('r', Items.GLOWSTONE_DUST)
                .define('n', Items.GOLD_NUGGET)
                .group("blank_module")
                .unlockedBy("has_scanner", InventoryChangeTrigger.TriggerInstance.hasItems(SCANNER.get()))
                .save(consumer);

        registerModule(RANGE_MODULE.get(), Items.ENDER_PEARL).save(consumer);
        registerModule(ENTITY_MODULE.get(), Items.LEAD).save(consumer);
        registerModule(FRIENDLY_ENTITY_MODULE.get(), Items.LEATHER).save(consumer);
        registerModule(HOSTILE_ENTITY_MODULE.get(), Items.BONE).save(consumer);
        registerModule(BLOCK_MODULE.get(), Items.STONE).save(consumer);
        registerModule(COMMON_ORES_MODULE.get(), Items.COAL).save(consumer);
        registerModule(RARE_ORES_MODULE.get(), Items.DIAMOND).save(consumer);
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
