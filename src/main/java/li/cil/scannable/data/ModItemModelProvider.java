package li.cil.scannable.data;

import li.cil.scannable.api.API;
import li.cil.scannable.common.item.Items;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public final class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(final DataGenerator generator, final ExistingFileHelper existingFileHelper) {
        super(generator, API.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        begin(Items.SCANNER.get())
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
        return withExistingParent(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)).getPath(), new ResourceLocation("item/generated"));
    }

    private void registerModule(final Item item) {
        begin(item)
                .texture("layer0", new ResourceLocation(API.MOD_ID, "items/blank_module"))
                .texture("layer1", new ResourceLocation(API.MOD_ID, "items/module_slot"))
                .texture("layer2", new ResourceLocation(API.MOD_ID, "items/" + Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)).getPath()));
    }
}
