package li.cil.scannable.data.fabric;

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
                .forceAddTag(BlockTags.GOLD_ORES)
                .forceAddTag(BlockTags.IRON_ORES)
                .forceAddTag(BlockTags.DIAMOND_ORES)
                .forceAddTag(BlockTags.REDSTONE_ORES)
                .forceAddTag(BlockTags.LAPIS_ORES)
                .forceAddTag(BlockTags.COAL_ORES)
                .forceAddTag(BlockTags.EMERALD_ORES)
                .forceAddTag(BlockTags.COPPER_ORES);

        getOrCreateTagBuilder(TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation("c", "gold_ores"))).forceAddTag(BlockTags.GOLD_ORES);
        getOrCreateTagBuilder(TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation("c", "iron_ores"))).forceAddTag(BlockTags.IRON_ORES);
        getOrCreateTagBuilder(TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation("c", "diamond_ores"))).forceAddTag(BlockTags.DIAMOND_ORES);
        getOrCreateTagBuilder(TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation("c", "redstone_ores"))).forceAddTag(BlockTags.REDSTONE_ORES);
        getOrCreateTagBuilder(TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation("c", "lapis_ores"))).forceAddTag(BlockTags.LAPIS_ORES);
        getOrCreateTagBuilder(TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation("c", "coal_ores"))).forceAddTag(BlockTags.COAL_ORES);
        getOrCreateTagBuilder(TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation("c", "emerald_ores"))).forceAddTag(BlockTags.EMERALD_ORES);
        getOrCreateTagBuilder(TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation("c", "copper_ores"))).forceAddTag(BlockTags.COPPER_ORES);
    }
}
