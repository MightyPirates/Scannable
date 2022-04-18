package li.cil.scannable.data;

import li.cil.scannable.common.tags.ItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.LocationTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
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
                .define('i', ItemTags.IRON_INGOTS)
                .define('b', Items.IRON_BARS)
                .define('r', ItemTags.REDSTONE_DUSTS)
                .define('g', ItemTags.GOLD_INGOTS)
                .define('q', ItemTags.QUARTZ_GEMS)
                .group("scanner")
                .unlockedBy("is_delving", LocationTrigger.TriggerInstance.located(LocationPredicate.inFeature(BuiltinStructures.MINESHAFT)))
                .save(consumer);

        ShapedRecipeBuilder.shaped(BLANK_MODULE.get())
                .pattern("ggg")
                .pattern("crc")
                .pattern("cnc")
                .define('g', ItemTags.GREEN_DYES)
                .define('c', Items.CLAY_BALL)
                .define('r', ItemTags.GLOWSTONE_DUSTS)
                .define('n', ItemTags.GOLD_NUGGETS)
                .group("blank_module")
                .unlockedBy("has_scanner", InventoryChangeTrigger.TriggerInstance.hasItems(SCANNER.get()))
                .save(consumer);

        registerModule(RANGE_MODULE.get(), ItemTags.ENDER_PEARLS).save(consumer);
        registerModule(ENTITY_MODULE.get(), Items.LEAD).save(consumer);
        registerModule(FRIENDLY_ENTITY_MODULE.get(), ItemTags.LEATHER).save(consumer);
        registerModule(HOSTILE_ENTITY_MODULE.get(), ItemTags.BONES).save(consumer);
        registerModule(BLOCK_MODULE.get(), ItemTags.STONE).save(consumer);
        registerModule(COMMON_ORES_MODULE.get(), Items.COAL).save(consumer);
        registerModule(RARE_ORES_MODULE.get(), ItemTags.DIAMOND_GEMS).save(consumer);
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
