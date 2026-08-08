package li.cil.scannable.data.fabric;

import li.cil.scannable.common.tags.CommonTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;

import java.util.concurrent.CompletableFuture;

public final class ModBlockTagsProvider extends FabricTagProvider.BlockTagProvider {
    public ModBlockTagsProvider(final FabricDataOutput output, final CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void addTags(final HolderLookup.Provider provider) {
        getOrCreateTagBuilder(CommonTags.ORES)
            .forceAddTag(BlockTags.GOLD_ORES)
            .forceAddTag(BlockTags.IRON_ORES)
            .forceAddTag(BlockTags.DIAMOND_ORES)
            .forceAddTag(BlockTags.REDSTONE_ORES)
            .forceAddTag(BlockTags.LAPIS_ORES)
            .forceAddTag(BlockTags.COAL_ORES)
            .forceAddTag(BlockTags.EMERALD_ORES)
            .forceAddTag(BlockTags.COPPER_ORES);

        getOrCreateTagBuilder(CommonTags.ORES_GOLD).forceAddTag(BlockTags.GOLD_ORES);
        getOrCreateTagBuilder(CommonTags.ORES_IRON).forceAddTag(BlockTags.IRON_ORES);
        getOrCreateTagBuilder(CommonTags.ORES_DIAMOND).forceAddTag(BlockTags.DIAMOND_ORES);
        getOrCreateTagBuilder(CommonTags.ORES_REDSTONE).forceAddTag(BlockTags.REDSTONE_ORES);
        getOrCreateTagBuilder(CommonTags.ORES_LAPIS).forceAddTag(BlockTags.LAPIS_ORES);
        getOrCreateTagBuilder(CommonTags.ORES_COAL).forceAddTag(BlockTags.COAL_ORES);
        getOrCreateTagBuilder(CommonTags.ORES_EMERALD).forceAddTag(BlockTags.EMERALD_ORES);
        getOrCreateTagBuilder(CommonTags.ORES_COPPER).forceAddTag(BlockTags.COPPER_ORES);
    }
}
