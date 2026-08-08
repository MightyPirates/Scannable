package li.cil.scannable.data.neoforge;

import li.cil.scannable.common.tags.CommonTags;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;

import java.util.concurrent.CompletableFuture;

import static li.cil.scannable.common.item.Items.*;

public final class ModRecipeProvider extends RecipeProvider {
    private final CompletableFuture<HolderLookup.Provider> registries;

    public ModRecipeProvider(final PackOutput output, final CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
        this.registries = registries;
    }

    @Override
    protected void buildRecipes(final RecipeOutput consumer) {
        final HolderLookup.Provider lookup = registries.join();

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, SCANNER.get())
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
                lookup.lookupOrThrow(Registries.STRUCTURE).getOrThrow(BuiltinStructures.MINESHAFT))))
            .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BLANK_MODULE.get())
            .pattern("ggg")
            .pattern("crc")
            .pattern("cnc")
            .define('g', CommonTags.DYES_GREEN)
            .define('c', Items.CLAY_BALL)
            .define('r', CommonTags.DUSTS_GLOWSTONE)
            .define('n', CommonTags.NUGGETS_GOLD)
            .group("blank_module")
            .unlockedBy("has_scanner", InventoryChangeTrigger.TriggerInstance.hasItems(SCANNER.get()))
            .save(consumer);

        registerModule(RANGE_MODULE.get(), CommonTags.ENDER_PEARLS).save(consumer);
        registerModule(ENTITY_MODULE.get(), Items.LEAD).save(consumer);
        registerModule(FRIENDLY_ENTITY_MODULE.get(), CommonTags.LEATHERS).save(consumer);
        registerModule(HOSTILE_ENTITY_MODULE.get(), CommonTags.BONES).save(consumer);
        registerModule(BLOCK_MODULE.get(), CommonTags.STONES).save(consumer);
        registerModule(COMMON_ORES_MODULE.get(), Items.COAL).save(consumer);
        registerModule(RARE_ORES_MODULE.get(), CommonTags.GEMS_DIAMOND).save(consumer);
        registerModule(FLUID_MODULE.get(), Items.WATER_BUCKET).save(consumer);
        registerModule(CHEST_MODULE.get(), Items.CHEST).save(consumer);
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
