package li.cil.scannable.data;

import li.cil.scannable.common.Scannable;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;

public final class Tags extends ItemTagsProvider {
    public Tags(final DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void registerTags() {
        getBuilder(Scannable.MODULES).add(
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
