package li.cil.scannable.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;

public final class ModBlockTagsProvider extends FabricTagProvider.BlockTagProvider {
    public ModBlockTagsProvider(final FabricDataGenerator generator) {
        super(generator);
    }

    @Override
    protected void generateTags() {
        getOrCreateTagBuilder(TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation("c", "ores")))
                .addTag(BlockTags.GOLD_ORES)
                .addTag(BlockTags.IRON_ORES)
                .addTag(BlockTags.DIAMOND_ORES)
                .addTag(BlockTags.REDSTONE_ORES)
                .addTag(BlockTags.LAPIS_ORES)
                .addTag(BlockTags.COAL_ORES)
                .addTag(BlockTags.EMERALD_ORES)
                .addTag(BlockTags.COPPER_ORES);

        getOrCreateTagBuilder(TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation("c", "gold_ores"))).addTag(BlockTags.GOLD_ORES);
        getOrCreateTagBuilder(TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation("c", "iron_ores"))).addTag(BlockTags.IRON_ORES);
        getOrCreateTagBuilder(TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation("c", "diamond_ores"))).addTag(BlockTags.DIAMOND_ORES);
        getOrCreateTagBuilder(TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation("c", "redstone_ores"))).addTag(BlockTags.REDSTONE_ORES);
        getOrCreateTagBuilder(TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation("c", "lapis_ores"))).addTag(BlockTags.LAPIS_ORES);
        getOrCreateTagBuilder(TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation("c", "coal_ores"))).addTag(BlockTags.COAL_ORES);
        getOrCreateTagBuilder(TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation("c", "emerald_ores"))).addTag(BlockTags.EMERALD_ORES);
        getOrCreateTagBuilder(TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation("c", "copper_ores"))).addTag(BlockTags.COPPER_ORES);
    }
}
