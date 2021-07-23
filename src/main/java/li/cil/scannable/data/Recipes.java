package li.cil.scannable.data;

import li.cil.scannable.common.Scannable;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.advancements.criterion.PositionTrigger;
import net.minecraft.data.*;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.ITag;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public final class Recipes extends RecipeProvider {

    public Recipes(final DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void buildShapelessRecipes(final Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(Scannable.SCANNER.get())
                .pattern("i i")
                .pattern("brb")
                .pattern("gqg")
                .define('i', Tags.Items.INGOTS_IRON)
                .define('b', Items.IRON_BARS)
                .define('r', Tags.Items.DUSTS_REDSTONE)
                .define('g', Tags.Items.INGOTS_GOLD)
                .define('q', Tags.Items.GEMS_QUARTZ)
                .group("scanner")
                .unlockedBy("is_delving", PositionTrigger.Instance.located(LocationPredicate.inFeature(Structure.MINESHAFT)))
                .save(consumer);

        ShapedRecipeBuilder.shaped(Scannable.MODULE_BLANK.get())
                .pattern("ggg")
                .pattern("crc")
                .pattern("cnc")
                .define('g', Tags.Items.DYES_GREEN)
                .define('c', Items.CLAY_BALL)
                .define('r', Tags.Items.DUSTS_GLOWSTONE)
                .define('n', Tags.Items.NUGGETS_GOLD)
                .group("blank_module")
                .unlockedBy("has_scanner", InventoryChangeTrigger.Instance.hasItems(Scannable.SCANNER.get()))
                .save(consumer);

        registerModule(Scannable.MODULE_RANGE.get(), Tags.Items.ENDER_PEARLS).save(consumer);
        registerModule(Scannable.MODULE_ENTITY.get(), Items.LEAD).save(consumer);
        registerModule(Scannable.MODULE_ANIMAL.get(), Tags.Items.LEATHER).save(consumer);
        registerModule(Scannable.MODULE_MONSTER.get(), Tags.Items.BONES).save(consumer);
        registerModule(Scannable.MODULE_BLOCK.get(), Tags.Items.STONE).save(consumer);
        registerModule(Scannable.MODULE_ORE_COMMON.get(), Items.COAL).save(consumer);
        registerModule(Scannable.MODULE_ORE_RARE.get(), Tags.Items.GEMS_DIAMOND).save(consumer);
        registerModule(Scannable.MODULE_FLUID.get(), Items.WATER_BUCKET).save(consumer);
//        registerModule(Scannable.MODULE_STRUCTURE.get(), Tags.Items.GEMS_EMERALD).build(consumer);
    }

    private static ShapelessRecipeBuilder registerModule(final Item item, final ITag<Item> ingredient) {
        return ShapelessRecipeBuilder.shapeless(item)
                .requires(Scannable.MODULE_BLANK.get())
                .requires(ingredient)
                .group("scanner_module")
                .unlockedBy("has_blank_module", InventoryChangeTrigger.Instance.hasItems(Scannable.MODULE_BLANK.get()));
    }

    private static ShapelessRecipeBuilder registerModule(final Item item, final Item ingredient) {
        return ShapelessRecipeBuilder.shapeless(item)
                .requires(Scannable.MODULE_BLANK.get())
                .requires(ingredient)
                .group("scanner_module")
                .unlockedBy("has_blank_module", InventoryChangeTrigger.Instance.hasItems(Scannable.MODULE_BLANK.get()));
    }
}
