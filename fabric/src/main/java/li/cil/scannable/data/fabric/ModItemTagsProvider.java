package li.cil.scannable.data.fabric;

import li.cil.scannable.common.item.Items;
import li.cil.scannable.common.tags.ItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.concurrent.CompletableFuture;

public final class ModItemTagsProvider extends FabricTagProvider.ItemTagProvider {
    public ModItemTagsProvider(final FabricDataOutput output, final CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void addTags(final HolderLookup.Provider provider) {
        tag(ItemTags.MODULES)
            .add(key(Items.BLANK_MODULE.get()))
            .add(key(Items.RANGE_MODULE.get()))
            .add(key(Items.ENTITY_MODULE.get()))
            .add(key(Items.FRIENDLY_ENTITY_MODULE.get()))
            .add(key(Items.HOSTILE_ENTITY_MODULE.get()))
            .add(key(Items.BLOCK_MODULE.get()))
            .add(key(Items.COMMON_ORES_MODULE.get()))
            .add(key(Items.RARE_ORES_MODULE.get()))
            .add(key(Items.FLUID_MODULE.get()))
            .add(key(Items.CHEST_MODULE.get()));
    }

    private static ResourceKey<Item> key(final Item item) {
        return BuiltInRegistries.ITEM.getResourceKey(item).orElseThrow();
    }
}
