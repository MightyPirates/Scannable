package li.cil.scannable.data;

import li.cil.scannable.api.API;
import li.cil.scannable.common.Scannable;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;

import java.util.Objects;

public class Items extends ItemModelProvider {
    public Items(final DataGenerator generator, final ExistingFileHelper existingFileHelper) {
        super(generator, API.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        begin(Scannable.SCANNER.get())
                .texture("layer0", new ResourceLocation(API.MOD_ID, "items/scanner"));
        begin(Scannable.MODULE_BLANK.get())
                .texture("layer0", new ResourceLocation(API.MOD_ID, "items/module_blank"));

        registerModule(Scannable.MODULE_RANGE.get(), "items/module_range");
        registerModule(Scannable.MODULE_ENTITY.get(), "items/module_entity");
        registerModule(Scannable.MODULE_ANIMAL.get(), "items/module_animal");
        registerModule(Scannable.MODULE_MONSTER.get(), "items/module_monster");
        registerModule(Scannable.MODULE_BLOCK.get(), "items/module_block");
        registerModule(Scannable.MODULE_ORE_COMMON.get(), "items/module_ore_common");
        registerModule(Scannable.MODULE_ORE_RARE.get(), "items/module_ore_rare");
        registerModule(Scannable.MODULE_FLUID.get(), "items/module_fluid");
        // registerModule(Scannable.MODULE_STRUCTURE.get(), "items/module_structure");
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
