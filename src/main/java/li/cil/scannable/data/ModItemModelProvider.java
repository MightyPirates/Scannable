package li.cil.scannable.data;

import li.cil.scannable.api.API;
import li.cil.scannable.common.item.Items;
import li.cil.scannable.mixin.client.ItemModelGeneratorAccessor;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.core.Registry;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.Objects;
import java.util.Optional;

public final class ModItemModelProvider extends FabricModelProvider {
    private static final TextureSlot LAYER1 = TextureSlot.create("layer1");
    private static final TextureSlot LAYER2 = TextureSlot.create("layer2");

    public ModItemModelProvider(final FabricDataGenerator generator) {
        super(generator);
    }

    @Override
    public void generateBlockStateModels(final BlockModelGenerators blockStateModelGenerator) {
    }

    @Override
    public void generateItemModels(final ItemModelGenerators itemModelGenerator) {
        registerSimpleItem(itemModelGenerator, Items.SCANNER.get());
        registerSimpleItem(itemModelGenerator, Items.BLANK_MODULE.get());

        registerModule(itemModelGenerator, Items.RANGE_MODULE.get());
        registerModule(itemModelGenerator, Items.ENTITY_MODULE.get());
        registerModule(itemModelGenerator, Items.FRIENDLY_ENTITY_MODULE.get());
        registerModule(itemModelGenerator, Items.HOSTILE_ENTITY_MODULE.get());
        registerModule(itemModelGenerator, Items.BLOCK_MODULE.get());
        registerModule(itemModelGenerator, Items.COMMON_ORES_MODULE.get());
        registerModule(itemModelGenerator, Items.RARE_ORES_MODULE.get());
        registerModule(itemModelGenerator, Items.FLUID_MODULE.get());
    }

    private void registerSimpleItem(final ItemModelGenerators itemModelGenerator, final Item item) {
        ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(item),
            TextureMapping.layer0(new ResourceLocation(API.MOD_ID, "items/" + Objects.requireNonNull(Registry.ITEM.getKey(item).getPath()))),
            ((ItemModelGeneratorAccessor) itemModelGenerator).getOutput());
    }

    private void registerModule(final ItemModelGenerators itemModelGenerator, final Item item) {
        final ModelTemplate model = new ModelTemplate(Optional.of(new ResourceLocation("minecraft", "item/generated")), Optional.empty(), TextureSlot.LAYER0, LAYER1, LAYER2);
        model.create(ModelLocationUtils.getModelLocation(item), (new TextureMapping())
                .put(TextureSlot.LAYER0, new ResourceLocation(API.MOD_ID, "items/blank_module"))
                .put(LAYER1, new ResourceLocation(API.MOD_ID, "items/module_slot"))
                .put(LAYER2, new ResourceLocation(API.MOD_ID, "items/" + Objects.requireNonNull(Registry.ITEM.getKey(item).getPath()))),
            ((ItemModelGeneratorAccessor) itemModelGenerator).getOutput());
    }
}
