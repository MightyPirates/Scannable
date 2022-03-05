package li.cil.scannable.data;

import li.cil.scannable.common.item.Items;
import li.cil.scannable.common.tags.ItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;

public final class ModItemTagsProvider extends FabricTagProvider.ItemTagProvider {
    public ModItemTagsProvider(final FabricDataGenerator generator, final FabricTagProvider.BlockTagProvider blockTagsProvider) {
        super(generator, blockTagsProvider);
    }

    @Override
    protected void generateTags() {
        tag(ItemTags.MODULES).add(
                Items.BLANK_MODULE.get(),
                Items.RANGE_MODULE.get(),
                Items.ENTITY_MODULE.get(),
                Items.FRIENDLY_ENTITY_MODULE.get(),
                Items.HOSTILE_ENTITY_MODULE.get(),
                Items.BLOCK_MODULE.get(),
                Items.COMMON_ORES_MODULE.get(),
                Items.RARE_ORES_MODULE.get(),
                Items.FLUID_MODULE.get()
        );
    }
}
