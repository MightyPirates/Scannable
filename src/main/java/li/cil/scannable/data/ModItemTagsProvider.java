package li.cil.scannable.data;

import li.cil.scannable.api.API;
import li.cil.scannable.common.Scannable;
import li.cil.scannable.common.tags.ItemTags;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public final class ModItemTagsProvider extends ItemTagsProvider {
    public ModItemTagsProvider(final DataGenerator generator, final BlockTagsProvider blockTagsProvider, final ExistingFileHelper existingFileHelper) {
        super(generator, blockTagsProvider, API.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(ItemTags.MODULES).add(
                Scannable.MODULE_BLANK.get(),
                Scannable.MODULE_RANGE.get(),
                Scannable.MODULE_ENTITY.get(),
                Scannable.MODULE_ANIMAL.get(),
                Scannable.MODULE_MONSTER.get(),
                Scannable.MODULE_BLOCK.get(),
                Scannable.MODULE_ORE_COMMON.get(),
                Scannable.MODULE_ORE_RARE.get(),
                Scannable.MODULE_FLUID.get()
                // Scannable.MODULE_STRUCTURE.get()
        );
    }
}
