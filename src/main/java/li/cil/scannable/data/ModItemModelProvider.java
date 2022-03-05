package li.cil.scannable.data;

import li.cil.scannable.api.API;
import li.cil.scannable.common.item.Items;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.models.ModelProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.Objects;

/*
public final class ModItemModelProvider extends ModelProvider {
    public ModItemModelProvider(final DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void registerModels() {
        begin(Items.SCANNER.get()
                .texture("layer0", new ResourceLocation(API.MOD_ID, "items/scanner"));
        begin(Items.BLANK_MODULE.get())
                .texture("layer0", new ResourceLocation(API.MOD_ID, "items/blank_module"));

        registerModule(Items.RANGE_MODULE.get());
        registerModule(Items.ENTITY_MODULE.get());
        registerModule(Items.FRIENDLY_ENTITY_MODULE.get());
        registerModule(Items.HOSTILE_ENTITY_MODULE.get());
        registerModule(Items.BLOCK_MODULE.get());
        registerModule(Items.COMMON_ORES_MODULE.get());
        registerModule(Items.RARE_ORES_MODULE.get());
        registerModule(Items.FLUID_MODULE.get());
    }

    private ItemModelBuilder begin(final Item item) {
        return withExistingParent(Objects.requireNonNull(item.getRegistryName()).getPath(), new ResourceLocation("item/generated"));
    }

    private void registerModule(final Item item) {
        begin(item)
                .texture("layer0", new ResourceLocation(API.MOD_ID, "items/blank_module"))
                .texture("layer1", new ResourceLocation(API.MOD_ID, "items/module_slot"))
                .texture("layer2", new ResourceLocation(API.MOD_ID, "items/" + Objects.requireNonNull(item.getRegistryName()).getPath()));
    }
}
*/
