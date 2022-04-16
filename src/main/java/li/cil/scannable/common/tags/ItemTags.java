package li.cil.scannable.common.tags;

import li.cil.scannable.api.API;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.tags.Tag;
import net.minecraft.resources.ResourceLocation;

public final class ItemTags {
    public static final TagKey<Item> MODULES = tag("modules");

    // --------------------------------------------------------------------- //

    public static void initialize() {
    }

    // --------------------------------------------------------------------- //

    private static TagKey<Item> tag(final String name) {
        return TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(API.MOD_ID, name));
    }
}
