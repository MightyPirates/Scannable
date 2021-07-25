package li.cil.scannable.data;

import li.cil.scannable.api.API;
import li.cil.scannable.common.item.Items;
import li.cil.scannable.common.tags.ItemTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public final class ModItemTagsProvider extends ItemTagsProvider {
    public ModItemTagsProvider(final DataGenerator generator, final BlockTagsProvider blockTagsProvider, final ExistingFileHelper existingFileHelper) {
        super(generator, blockTagsProvider, API.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags() {
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
                // Items.STRUCTURES_MODULE.get()
        );
    }
}
