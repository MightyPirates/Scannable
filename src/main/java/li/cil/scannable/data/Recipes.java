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
    protected void registerRecipes(final Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shapedRecipe(Scannable.SCANNER.get())
                .patternLine("i i")
                .patternLine("brb")
                .patternLine("gqg")
                .key('i', Tags.Items.INGOTS_IRON)
                .key('b', Items.IRON_BARS)
                .key('r', Tags.Items.DUSTS_REDSTONE)
                .key('g', Tags.Items.INGOTS_GOLD)
                .key('q', Tags.Items.GEMS_QUARTZ)
                .setGroup("scanner")
                .addCriterion("is_delving", PositionTrigger.Instance.forLocation(LocationPredicate.forFeature(Structure.MINESHAFT)))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(Scannable.MODULE_BLANK.get())
                .patternLine("ggg")
                .patternLine("crc")
                .patternLine("cnc")
                .key('g', Tags.Items.DYES_GREEN)
                .key('c', Items.CLAY_BALL)
                .key('r', Tags.Items.DUSTS_GLOWSTONE)
                .key('n', Tags.Items.NUGGETS_GOLD)
                .setGroup("blank_module")
                .addCriterion("has_scanner", InventoryChangeTrigger.Instance.forItems(Scannable.SCANNER.get()))
                .build(consumer);

        registerModule(Scannable.MODULE_RANGE.get(), Tags.Items.ENDER_PEARLS).build(consumer);
        registerModule(Scannable.MODULE_ENTITY.get(), Items.LEAD).build(consumer);
        registerModule(Scannable.MODULE_ANIMAL.get(), Tags.Items.LEATHER).build(consumer);
        registerModule(Scannable.MODULE_MONSTER.get(), Tags.Items.BONES).build(consumer);
        registerModule(Scannable.MODULE_BLOCK.get(), Tags.Items.STONE).build(consumer);
        registerModule(Scannable.MODULE_ORE_COMMON.get(), Items.COAL).build(consumer);
        registerModule(Scannable.MODULE_ORE_RARE.get(), Tags.Items.GEMS_DIAMOND).build(consumer);
        registerModule(Scannable.MODULE_FLUID.get(), Items.WATER_BUCKET).build(consumer);
//        registerModule(Scannable.MODULE_STRUCTURE.get(), Tags.Items.GEMS_EMERALD).build(consumer);
    }

    private static ShapelessRecipeBuilder registerModule(final Item item, final ITag<Item> ingredient) {
        return ShapelessRecipeBuilder.shapelessRecipe(item)
                .addIngredient(Scannable.MODULE_BLANK.get())
                .addIngredient(ingredient)
                .setGroup("scanner_module")
                .addCriterion("has_blank_module", InventoryChangeTrigger.Instance.forItems(Scannable.MODULE_BLANK.get()));
    }

    private static ShapelessRecipeBuilder registerModule(final Item item, final Item ingredient) {
        return ShapelessRecipeBuilder.shapelessRecipe(item)
                .addIngredient(Scannable.MODULE_BLANK.get())
                .addIngredient(ingredient)
                .setGroup("scanner_module")
                .addCriterion("has_blank_module", InventoryChangeTrigger.Instance.forItems(Scannable.MODULE_BLANK.get()));
    }
}
