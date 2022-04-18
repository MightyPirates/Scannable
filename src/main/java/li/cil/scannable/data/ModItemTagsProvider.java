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

        tag(ItemTags.IRON_INGOTS).add(net.minecraft.world.item.Items.IRON_INGOT);
        tag(ItemTags.ENDER_PEARLS).add(net.minecraft.world.item.Items.ENDER_PEARL);
        tag(ItemTags.GOLD_NUGGETS).add(net.minecraft.world.item.Items.GOLD_NUGGET);
        tag(ItemTags.GOLD_INGOTS).add(net.minecraft.world.item.Items.GOLD_INGOT);
        tag(ItemTags.LEATHER).add(net.minecraft.world.item.Items.LEATHER);
        tag(ItemTags.REDSTONE_DUSTS).add(net.minecraft.world.item.Items.REDSTONE);
        tag(ItemTags.IRON_INGOTS).add(net.minecraft.world.item.Items.IRON_INGOT);
        tag(ItemTags.QUARTZ_GEMS).add(net.minecraft.world.item.Items.QUARTZ);
        tag(ItemTags.GREEN_DYES).add(net.minecraft.world.item.Items.GREEN_DYE);
        tag(ItemTags.GLOWSTONE_DUSTS).add(net.minecraft.world.item.Items.GLOWSTONE_DUST);
        tag(ItemTags.BONES).add(net.minecraft.world.item.Items.BONE);
        tag(ItemTags.STONE).add(net.minecraft.world.item.Items.STONE);
        tag(ItemTags.DIAMOND_GEMS).add(net.minecraft.world.item.Items.DIAMOND);
    }
}
