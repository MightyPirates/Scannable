package li.cil.scannable.common.tags;

import li.cil.scannable.api.API;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class ItemTags {
    public static final TagKey<Item> MODULES = tag("modules");

    // --------------------------------------------------------------------- //

    public static void initialize() {
    }

    // --------------------------------------------------------------------- //

    private static TagKey<Item> tag(final String name) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(API.MOD_ID, name));
    }
}
