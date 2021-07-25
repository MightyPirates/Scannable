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
                Items.MODULE_BLANK.get(),
                Items.MODULE_RANGE.get(),
                Items.MODULE_ENTITY.get(),
                Items.MODULE_ANIMAL.get(),
                Items.MODULE_MONSTER.get(),
                Items.MODULE_BLOCK.get(),
                Items.MODULE_ORE_COMMON.get(),
                Items.MODULE_ORE_RARE.get(),
                Items.MODULE_FLUID.get()
                // Items.MODULE_STRUCTURE.get()
        );
    }
}
