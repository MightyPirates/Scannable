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
        tag(ItemTags.MODULES).add(
            key(Items.BLANK_MODULE.get()),
            key(Items.RANGE_MODULE.get()),
            key(Items.ENTITY_MODULE.get()),
            key(Items.FRIENDLY_ENTITY_MODULE.get()),
            key(Items.HOSTILE_ENTITY_MODULE.get()),
            key(Items.BLOCK_MODULE.get()),
            key(Items.COMMON_ORES_MODULE.get()),
            key(Items.RARE_ORES_MODULE.get()),
            key(Items.FLUID_MODULE.get())
        );

        tag(CommonItemTags.IRON_INGOTS).add(key(net.minecraft.world.item.Items.IRON_INGOT));
        tag(CommonItemTags.ENDER_PEARLS).add(key(net.minecraft.world.item.Items.ENDER_PEARL));
        tag(CommonItemTags.GOLD_NUGGETS).add(key(net.minecraft.world.item.Items.GOLD_NUGGET));
        tag(CommonItemTags.GOLD_INGOTS).add(key(net.minecraft.world.item.Items.GOLD_INGOT));
        tag(CommonItemTags.LEATHER).add(key(net.minecraft.world.item.Items.LEATHER));
        tag(CommonItemTags.REDSTONE_DUSTS).add(key(net.minecraft.world.item.Items.REDSTONE));
        tag(CommonItemTags.IRON_INGOTS).add(key(net.minecraft.world.item.Items.IRON_INGOT));
        tag(CommonItemTags.QUARTZ_GEMS).add(key(net.minecraft.world.item.Items.QUARTZ));
        tag(CommonItemTags.GREEN_DYES).add(key(net.minecraft.world.item.Items.GREEN_DYE));
        tag(CommonItemTags.GLOWSTONE_DUSTS).add(key(net.minecraft.world.item.Items.GLOWSTONE_DUST));
        tag(CommonItemTags.BONES).add(key(net.minecraft.world.item.Items.BONE));
        tag(CommonItemTags.STONE).add(key(net.minecraft.world.item.Items.STONE));
        tag(CommonItemTags.DIAMOND_GEMS).add(key(net.minecraft.world.item.Items.DIAMOND));
    }

    private static ResourceKey<Item> key(final Item item) {
        return BuiltInRegistries.ITEM.getResourceKey(item).orElseThrow();
    }
}
