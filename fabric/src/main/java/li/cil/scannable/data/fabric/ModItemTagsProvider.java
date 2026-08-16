package li.cil.scannable.data.fabric;

import li.cil.scannable.common.item.Items;
import li.cil.scannable.common.tags.CommonTags;
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

        valueLookupBuilder(CommonTags.INGOTS_IRON).add(net.minecraft.world.item.Items.IRON_INGOT);
        valueLookupBuilder(CommonTags.ENDER_PEARLS).add(net.minecraft.world.item.Items.ENDER_PEARL);
        valueLookupBuilder(CommonTags.NUGGETS_GOLD).add(net.minecraft.world.item.Items.GOLD_NUGGET);
        valueLookupBuilder(CommonTags.INGOTS_GOLD).add(net.minecraft.world.item.Items.GOLD_INGOT);
        valueLookupBuilder(CommonTags.LEATHERS).add(net.minecraft.world.item.Items.LEATHER);
        valueLookupBuilder(CommonTags.DUSTS_REDSTONE).add(net.minecraft.world.item.Items.REDSTONE);
        valueLookupBuilder(CommonTags.GEMS_QUARTZ).add(net.minecraft.world.item.Items.QUARTZ);
        valueLookupBuilder(CommonTags.DYES_GREEN).add(net.minecraft.world.item.Items.GREEN_DYE);
        valueLookupBuilder(CommonTags.DUSTS_GLOWSTONE).add(net.minecraft.world.item.Items.GLOWSTONE_DUST);
        valueLookupBuilder(CommonTags.BONES).add(net.minecraft.world.item.Items.BONE);
        valueLookupBuilder(CommonTags.STONES).add(net.minecraft.world.item.Items.STONE);
        valueLookupBuilder(CommonTags.GEMS_DIAMOND).add(net.minecraft.world.item.Items.DIAMOND);
    }
}
