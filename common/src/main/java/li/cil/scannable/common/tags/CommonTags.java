package li.cil.scannable.common.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/**
 * Convention ("{@code c}") tags shared by Fabric and NeoForge.
 * <p>
 * As of MC 1.21 both loaders use the same {@code c} namespace and the same tag
 * names, so these no longer need a per-platform implementation.
 */
public final class CommonTags {
    public static final String NAMESPACE = "c";

    // --------------------------------------------------------------------- //
    // Blocks

    public static final TagKey<Block> ORES = blockTag("ores");

    public static final TagKey<Block> ORES_COAL = blockTag("ores/coal");
    public static final TagKey<Block> ORES_COPPER = blockTag("ores/copper");
    public static final TagKey<Block> ORES_DIAMOND = blockTag("ores/diamond");
    public static final TagKey<Block> ORES_EMERALD = blockTag("ores/emerald");
    public static final TagKey<Block> ORES_GOLD = blockTag("ores/gold");
    public static final TagKey<Block> ORES_IRON = blockTag("ores/iron");
    public static final TagKey<Block> ORES_LAPIS = blockTag("ores/lapis");
    public static final TagKey<Block> ORES_QUARTZ = blockTag("ores/quartz");
    public static final TagKey<Block> ORES_REDSTONE = blockTag("ores/redstone");

    // Not defined by either loader, but conventional names commonly used by mods.
    public static final TagKey<Block> ORES_LEAD = blockTag("ores/lead");
    public static final TagKey<Block> ORES_MITHRIL = blockTag("ores/mithril");
    public static final TagKey<Block> ORES_NICKEL = blockTag("ores/nickel");
    public static final TagKey<Block> ORES_PLATINUM = blockTag("ores/platinum");
    public static final TagKey<Block> ORES_SILVER = blockTag("ores/silver");
    public static final TagKey<Block> ORES_TIN = blockTag("ores/tin");

    public static final TagKey<Block> BARRELS_WOODEN = blockTag("barrels/wooden");
    public static final TagKey<Block> CHESTS = blockTag("chests");
    public static final TagKey<Block> SHULKER_BOXES = blockTag("shulker_boxes");

    // --------------------------------------------------------------------- //
    // Items

    public static final TagKey<Item> BONES = itemTag("bones");
    public static final TagKey<Item> DUSTS_GLOWSTONE = itemTag("dusts/glowstone");
    public static final TagKey<Item> DUSTS_REDSTONE = itemTag("dusts/redstone");
    public static final TagKey<Item> DYES_GREEN = itemTag("dyes/green");
    public static final TagKey<Item> ENDER_PEARLS = itemTag("ender_pearls");
    public static final TagKey<Item> GEMS_DIAMOND = itemTag("gems/diamond");
    public static final TagKey<Item> GEMS_QUARTZ = itemTag("gems/quartz");
    public static final TagKey<Item> INGOTS_GOLD = itemTag("ingots/gold");
    public static final TagKey<Item> INGOTS_IRON = itemTag("ingots/iron");
    public static final TagKey<Item> LEATHERS = itemTag("leathers");
    public static final TagKey<Item> NUGGETS_GOLD = itemTag("nuggets/gold");
    public static final TagKey<Item> STONES = itemTag("stones");

    // --------------------------------------------------------------------- //

    public static ResourceLocation id(final String name) {
        return ResourceLocation.fromNamespaceAndPath(NAMESPACE, name);
    }

    private static TagKey<Block> blockTag(final String name) {
        return TagKey.create(Registries.BLOCK, id(name));
    }

    private static TagKey<Item> itemTag(final String name) {
        return TagKey.create(Registries.ITEM, id(name));
    }

    private CommonTags() {
    }
}
