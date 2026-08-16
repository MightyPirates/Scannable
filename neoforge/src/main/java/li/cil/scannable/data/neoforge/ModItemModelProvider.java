package li.cil.scannable.data.neoforge;

import li.cil.scannable.api.API;
import li.cil.scannable.common.item.Items;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public final class ModItemModelProvider extends ModelProvider {
    private static final TextureSlot LAYER1 = TextureSlot.create("layer1");
    private static final TextureSlot LAYER2 = TextureSlot.create("layer2");

    public ModItemModelProvider(final PackOutput output) {
        super(output, API.MOD_ID);
    }

    @Override
    protected void registerModels(final BlockModelGenerators blockModels, final ItemModelGenerators itemModels) {
        registerSimpleItem(itemModels, Items.SCANNER.get());
        registerSimpleItem(itemModels, Items.BLANK_MODULE.get());

        registerModule(itemModels, Items.RANGE_MODULE.get());
        registerModule(itemModels, Items.ENTITY_MODULE.get());
        registerModule(itemModels, Items.FRIENDLY_ENTITY_MODULE.get());
        registerModule(itemModels, Items.HOSTILE_ENTITY_MODULE.get());
        registerModule(itemModels, Items.BLOCK_MODULE.get());
        registerModule(itemModels, Items.COMMON_ORES_MODULE.get());
        registerModule(itemModels, Items.RARE_ORES_MODULE.get());
        registerModule(itemModels, Items.FLUID_MODULE.get());
        registerModule(itemModels, Items.CHEST_MODULE.get());
    }

    @Override
    protected Stream<? extends net.minecraft.core.Holder<net.minecraft.world.level.block.Block>> getKnownBlocks() {
        return Stream.empty();
    }

    private static void registerSimpleItem(final ItemModelGenerators itemModels, final Item item) {
        final Identifier model = ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(item),
            TextureMapping.layer0(itemTexture(item)), itemModels.modelOutput);
        itemModels.itemModelOutput.accept(item, ItemModelUtils.plainModel(model));
    }

    private static void registerModule(final ItemModelGenerators itemModels, final Item item) {
        final ModelTemplate template = new ModelTemplate(Optional.of(Identifier.withDefaultNamespace("item/generated")), Optional.empty(), TextureSlot.LAYER0, LAYER1, LAYER2);
        final Identifier model = template.create(ModelLocationUtils.getModelLocation(item), new TextureMapping()
                .put(TextureSlot.LAYER0, modTexture("item/blank_module"))
                .put(LAYER1, modTexture("item/module_slot"))
                .put(LAYER2, itemTexture(item)),
            itemModels.modelOutput);
        itemModels.itemModelOutput.accept(item, ItemModelUtils.plainModel(model));
    }

    private static Identifier itemTexture(final Item item) {
        return modTexture("item/" + Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item)).getPath());
    }

    private static Identifier modTexture(final String path) {
        return Identifier.fromNamespaceAndPath(API.MOD_ID, path);
    }
}
