package li.cil.scannable.data.fabric;

import li.cil.scannable.common.item.Items;
import li.cil.scannable.common.tags.ItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public final class ModItemTagsProvider extends FabricTagProvider.ItemTagProvider {
    public ModItemTagsProvider(final FabricDataOutput output, final CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void addTags(final HolderLookup.Provider provider) {
        valueLookupBuilder(ItemTags.MODULES)
            .add(Items.BLANK_MODULE.get())
            .add(Items.RANGE_MODULE.get())
            .add(Items.ENTITY_MODULE.get())
            .add(Items.FRIENDLY_ENTITY_MODULE.get())
            .add(Items.HOSTILE_ENTITY_MODULE.get())
            .add(Items.BLOCK_MODULE.get())
            .add(Items.COMMON_ORES_MODULE.get())
            .add(Items.RARE_ORES_MODULE.get())
            .add(Items.FLUID_MODULE.get())
            .add(Items.CHEST_MODULE.get());
    }
}
