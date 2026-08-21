/* SPDX-License-Identifier: MIT */

package li.cil.scannable.data.neoforge;

import li.cil.scannable.api.API;
import li.cil.scannable.common.item.Items;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.Objects;

public final class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(final PackOutput output, final ExistingFileHelper existingFileHelper) {
        super(output, API.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        begin(Items.SCANNER.get())
            .texture("layer0", ResourceLocation.fromNamespaceAndPath(API.MOD_ID, "item/scanner"));
        begin(Items.BLANK_MODULE.get())
            .texture("layer0", ResourceLocation.fromNamespaceAndPath(API.MOD_ID, "item/blank_module"));

        registerModule(Items.RANGE_MODULE.get());
        registerModule(Items.ENTITY_MODULE.get());
        registerModule(Items.FRIENDLY_ENTITY_MODULE.get());
        registerModule(Items.HOSTILE_ENTITY_MODULE.get());
        registerModule(Items.BLOCK_MODULE.get());
        registerModule(Items.COMMON_ORES_MODULE.get());
        registerModule(Items.RARE_ORES_MODULE.get());
        registerModule(Items.FLUID_MODULE.get());
        registerModule(Items.CHEST_MODULE.get());
    }

    private ItemModelBuilder begin(final Item item) {
        return withExistingParent(Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item)).getPath(), ResourceLocation.withDefaultNamespace("item/generated"));
    }

    private void registerModule(final Item item) {
        begin(item)
            .texture("layer0", ResourceLocation.fromNamespaceAndPath(API.MOD_ID, "item/blank_module"))
            .texture("layer1", ResourceLocation.fromNamespaceAndPath(API.MOD_ID, "item/module_slot"))
            .texture("layer2", ResourceLocation.fromNamespaceAndPath(API.MOD_ID, "item/" + Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item)).getPath()));
    }
}
