package li.cil.scannable.data.fabric;

import li.cil.scannable.common.tags.CommonTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.advancements.criterion.PlayerTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;

import java.util.concurrent.CompletableFuture;

import static li.cil.scannable.common.item.Items.*;

public final class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(final FabricDataOutput output, final CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected RecipeProvider createRecipeProvider(final HolderLookup.Provider registries, final RecipeOutput output) {
        return new RecipeProvider(registries, output) {
            @Override
            public void buildRecipes() {
                final HolderGetter<Item> items = registries.lookupOrThrow(Registries.ITEM);

                ShapedRecipeBuilder.shaped(items, RecipeCategory.TOOLS, SCANNER.get())
                    .pattern("i i")
                    .pattern("brb")
                    .pattern("gqg")
                    .define('i', CommonTags.INGOTS_IRON)
                    .define('b', Items.IRON_BARS)
                    .define('r', CommonTags.DUSTS_REDSTONE)
                    .define('g', CommonTags.INGOTS_GOLD)
                    .define('q', CommonTags.GEMS_QUARTZ)
                    .group("scanner")
                    .unlockedBy("is_delving", PlayerTrigger.TriggerInstance.located(LocationPredicate.Builder.inStructure(
                        registries.lookupOrThrow(Registries.STRUCTURE).getOrThrow(BuiltinStructures.MINESHAFT))))
                    .save(output);

                ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, BLANK_MODULE.get())
                    .pattern("ggg")
                    .pattern("crc")
                    .pattern("cnc")
                    .define('g', CommonTags.DYES_GREEN)
                    .define('c', Items.CLAY_BALL)
                    .define('r', CommonTags.DUSTS_GLOWSTONE)
                    .define('n', CommonTags.NUGGETS_GOLD)
                    .group("blank_module")
                    .unlockedBy("has_scanner", InventoryChangeTrigger.TriggerInstance.hasItems(SCANNER.get()))
                    .save(output);

                registerModule(items, RANGE_MODULE.get(), CommonTags.ENDER_PEARLS).save(output);
                registerModule(items, ENTITY_MODULE.get(), Items.LEAD).save(output);
                registerModule(items, FRIENDLY_ENTITY_MODULE.get(), CommonTags.LEATHERS).save(output);
                registerModule(items, HOSTILE_ENTITY_MODULE.get(), CommonTags.BONES).save(output);
                registerModule(items, BLOCK_MODULE.get(), CommonTags.STONES).save(output);
                registerModule(items, COMMON_ORES_MODULE.get(), Items.COAL).save(output);
                registerModule(items, RARE_ORES_MODULE.get(), CommonTags.GEMS_DIAMOND).save(output);
                registerModule(items, FLUID_MODULE.get(), Items.WATER_BUCKET).save(output);
                registerModule(items, CHEST_MODULE.get(), Items.CHEST).save(output);
            }
        };
    }

    @Override
    public String getName() {
        return "Scannable Recipes";
    }

    private static ShapelessRecipeBuilder registerModule(final HolderGetter<Item> items, final Item item, final TagKey<Item> ingredient) {
        return finishModule(ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, item)
            .requires(BLANK_MODULE.get())
            .requires(ingredient));
    }

    private static ShapelessRecipeBuilder registerModule(final HolderGetter<Item> items, final Item item, final Item ingredient) {
        return finishModule(ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, item)
            .requires(BLANK_MODULE.get())
            .requires(ingredient));
    }

    private static ShapelessRecipeBuilder finishModule(final ShapelessRecipeBuilder builder) {
        return builder
            .group("scanner_module")
            .unlockedBy("has_blank_module", InventoryChangeTrigger.TriggerInstance.hasItems(BLANK_MODULE.get()));
    }
}
