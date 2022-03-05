package li.cil.scannable.data;

import li.cil.scannable.api.API;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.TagFactory;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;

public final class ModBlockTagsProvider extends FabricTagProvider.BlockTagProvider {
    public ModBlockTagsProvider(final FabricDataGenerator generator) {
        super(generator);
    }

    @Override
    protected void generateTags() {
        getOrCreateTagBuilder(TagFactory.BLOCK.create(new ResourceLocation("c", "ores")))
                .addTag(BlockTags.GOLD_ORES)
                .addTag(BlockTags.IRON_ORES)
                .addTag(BlockTags.DIAMOND_ORES)
                .addTag(BlockTags.REDSTONE_ORES)
                .addTag(BlockTags.LAPIS_ORES)
                .addTag(BlockTags.COAL_ORES)
                .addTag(BlockTags.EMERALD_ORES)
                .addTag(BlockTags.COPPER_ORES);

        getOrCreateTagBuilder(TagFactory.BLOCK.create(new ResourceLocation("c", "gold_ores"))).addTag(BlockTags.GOLD_ORES);
        getOrCreateTagBuilder(TagFactory.BLOCK.create(new ResourceLocation("c", "iron_ores"))).addTag(BlockTags.IRON_ORES);
        getOrCreateTagBuilder(TagFactory.BLOCK.create(new ResourceLocation("c", "diamond_ores"))).addTag(BlockTags.DIAMOND_ORES);
        getOrCreateTagBuilder(TagFactory.BLOCK.create(new ResourceLocation("c", "redstone_ores"))).addTag(BlockTags.REDSTONE_ORES);
        getOrCreateTagBuilder(TagFactory.BLOCK.create(new ResourceLocation("c", "lapis_ores"))).addTag(BlockTags.LAPIS_ORES);
        getOrCreateTagBuilder(TagFactory.BLOCK.create(new ResourceLocation("c", "coal_ores"))).addTag(BlockTags.COAL_ORES);
        getOrCreateTagBuilder(TagFactory.BLOCK.create(new ResourceLocation("c", "emerald_ores"))).addTag(BlockTags.EMERALD_ORES);
        getOrCreateTagBuilder(TagFactory.BLOCK.create(new ResourceLocation("c", "copper_ores"))).addTag(BlockTags.COPPER_ORES);
    }
}
