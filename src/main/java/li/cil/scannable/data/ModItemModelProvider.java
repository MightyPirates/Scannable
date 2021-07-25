package li.cil.scannable.data;

import li.cil.scannable.api.API;
import li.cil.scannable.common.item.Items;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.Objects;

public final class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(final DataGenerator generator, final ExistingFileHelper existingFileHelper) {
        super(generator, API.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        begin(Items.SCANNER.get())
                .texture("layer0", new ResourceLocation(API.MOD_ID, "items/scanner"));
        begin(Items.MODULE_BLANK.get())
                .texture("layer0", new ResourceLocation(API.MOD_ID, "items/module_blank"));

        registerModule(Items.MODULE_RANGE.get(), "items/module_range");
        registerModule(Items.MODULE_ENTITY.get(), "items/module_entity");
        registerModule(Items.MODULE_ANIMAL.get(), "items/module_animal");
        registerModule(Items.MODULE_MONSTER.get(), "items/module_monster");
        registerModule(Items.MODULE_BLOCK.get(), "items/module_block");
        registerModule(Items.MODULE_ORE_COMMON.get(), "items/module_ore_common");
        registerModule(Items.MODULE_ORE_RARE.get(), "items/module_ore_rare");
        registerModule(Items.MODULE_FLUID.get(), "items/module_fluid");
        // registerModule(Items.MODULE_STRUCTURE.get(), "items/module_structure");
    }

    private ItemModelBuilder begin(final Item item) {
        return withExistingParent(Objects.requireNonNull(item.getRegistryName()).getPath(), new ResourceLocation("item/generated"));
    }

    private void registerModule(final Item item, final String texture) {
        begin(item)
                .texture("layer0", new ResourceLocation(API.MOD_ID, "items/module_blank"))
                .texture("layer1", new ResourceLocation(API.MOD_ID, "items/module_slot"))
                .texture("layer2", new ResourceLocation(API.MOD_ID, texture));
    }
}
