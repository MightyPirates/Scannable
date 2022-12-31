package li.cil.scannable.data.forge;

import li.cil.scannable.api.API;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public final class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(final PackOutput output, final CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable final ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, API.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(final HolderLookup.Provider provider) {
        tag(Tags.Blocks.ORES)
            .addTag(BlockTags.GOLD_ORES)
            .addTag(BlockTags.IRON_ORES)
            .addTag(BlockTags.DIAMOND_ORES)
            .addTag(BlockTags.REDSTONE_ORES)
            .addTag(BlockTags.LAPIS_ORES)
            .addTag(BlockTags.COAL_ORES)
            .addTag(BlockTags.EMERALD_ORES)
            .addTag(BlockTags.COPPER_ORES);

        tag(Tags.Blocks.ORES_GOLD).addTag(BlockTags.GOLD_ORES);
        tag(Tags.Blocks.ORES_IRON).addTag(BlockTags.IRON_ORES);
        tag(Tags.Blocks.ORES_DIAMOND).addTag(BlockTags.DIAMOND_ORES);
        tag(Tags.Blocks.ORES_REDSTONE).addTag(BlockTags.REDSTONE_ORES);
        tag(Tags.Blocks.ORES_LAPIS).addTag(BlockTags.LAPIS_ORES);
        tag(Tags.Blocks.ORES_COAL).addTag(BlockTags.COAL_ORES);
        tag(Tags.Blocks.ORES_EMERALD).addTag(BlockTags.EMERALD_ORES);
    }
}
