package li.cil.scannable.data.fabric;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class CommonItemTags {
    public static final TagKey<Item> IRON_INGOTS = commonTag("iron_ingots");
    public static final TagKey<Item> REDSTONE_DUSTS = commonTag("redstone_dusts");
    public static final TagKey<Item> GOLD_INGOTS = commonTag("gold_ingots");
    public static final TagKey<Item> QUARTZ_GEMS = commonTag("quartz");
    public static final TagKey<Item> GREEN_DYES = commonTag("dye_green");
    public static final TagKey<Item> GLOWSTONE_DUSTS = commonTag("glowstone_dusts");
    public static final TagKey<Item> GOLD_NUGGETS = commonTag("gold_nuggets");
    public static final TagKey<Item> ENDER_PEARLS = commonTag("ender_pearls");
    public static final TagKey<Item> LEATHER = commonTag("leather");
    public static final TagKey<Item> BONES = commonTag("bones");
    public static final TagKey<Item> STONE = commonTag("stone");
    public static final TagKey<Item> DIAMOND_GEMS = commonTag("diamonds");

    // --------------------------------------------------------------------- //

    private static TagKey<Item> commonTag(final String name) {
        return TagKey.create(Registries.ITEM, new ResourceLocation("c", name));
    }
}
