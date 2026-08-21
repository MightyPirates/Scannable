/* SPDX-License-Identifier: MIT */

package li.cil.scannable.data.neoforge;

import li.cil.scannable.api.API;
import li.cil.scannable.common.item.Items;
import li.cil.scannable.common.tags.ItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public final class ModItemTagsProvider extends ItemTagsProvider {
    public ModItemTagsProvider(final PackOutput output, final CompletableFuture<HolderLookup.Provider> lookupProvider, final CompletableFuture<TagLookup<Block>> blockTagsProvider, final ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTagsProvider, API.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(final HolderLookup.Provider provider) {
        tag(ItemTags.MODULES).add(
            Items.BLANK_MODULE.get(),
            Items.RANGE_MODULE.get(),
            Items.ENTITY_MODULE.get(),
            Items.FRIENDLY_ENTITY_MODULE.get(),
            Items.HOSTILE_ENTITY_MODULE.get(),
            Items.BLOCK_MODULE.get(),
            Items.COMMON_ORES_MODULE.get(),
            Items.RARE_ORES_MODULE.get(),
            Items.FLUID_MODULE.get(),
            Items.CHEST_MODULE.get()
        );
    }
}
