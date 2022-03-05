package li.cil.scannable.common.tags;

import li.cil.scannable.api.API;
import net.fabricmc.fabric.api.tag.TagFactory;
import net.minecraft.world.item.Item;
import net.minecraft.tags.Tag;
import net.minecraft.resources.ResourceLocation;

public final class ItemTags {
    public static final Tag.Named<Item> MODULES = tag("modules");

    // --------------------------------------------------------------------- //

    public static void initialize() {
    }

    // --------------------------------------------------------------------- //

    private static Tag.Named<Item> tag(final String name) {
        return TagFactory.ITEM.create(new ResourceLocation(API.MOD_ID, name));
    }
}
