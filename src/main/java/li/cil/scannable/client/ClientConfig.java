package li.cil.scannable.client;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import li.cil.scannable.util.ConfigManager.*;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.fml.config.ModConfig;

@Type(ModConfig.Type.CLIENT)
public final class ClientConfig {
    @WorldRestart
    @Comment("""
            The colors for blocks used when rendering their result bounding box
            by block name. Each entry must be a key-value pair separated by a `=`,
            with the key being the tag name and the value being the hexadecimal
            RGB value of the color.""")
    @KeyValueTypes(keyType = ResourceLocation.class, valueType = int.class,
            valueSerializer = @CustomSerializer(serializer = "toHexString", deserializer = "fromHexString"))
    public static Object2IntMap<ResourceLocation> blockColors = new Object2IntOpenHashMap<>();

    @WorldRestart
    @Comment("The colors for blocks used when rendering their result bounding box\n" +
             "by block tag. See `blockColors` for format entries have to be in.")
    @KeyValueTypes(keyType = ResourceLocation.class, valueType = int.class,
            valueSerializer = @CustomSerializer(serializer = "toHexString", deserializer = "fromHexString"))
    public static Object2IntMap<ResourceLocation> blockTagColors = Util.make(new Object2IntOpenHashMap<>(), c -> {
        // Minecraft
        c.put(new ResourceLocation("c", "coal_ores"), MaterialColor.COLOR_GRAY.col);
        c.put(new ResourceLocation("c", "iron_ores"), MaterialColor.COLOR_BROWN.col); // MaterialColor.IRON is also gray, so...
        c.put(new ResourceLocation("c", "gold_ores"), MaterialColor.GOLD.col);
        c.put(new ResourceLocation("c", "lapis_ores"), MaterialColor.LAPIS.col);
        c.put(new ResourceLocation("c", "diamond_ores"), MaterialColor.DIAMOND.col);
        c.put(new ResourceLocation("c", "redstone_ores"), MaterialColor.COLOR_RED.col);
        c.put(new ResourceLocation("c", "emerald_ores"), MaterialColor.EMERALD.col);
        c.put(new ResourceLocation("c", "quartz_ores"), MaterialColor.QUARTZ.col);

        // Common modded ores
        c.put(new ResourceLocation("c", "tin_ores"), MaterialColor.COLOR_CYAN.col);
        c.put(new ResourceLocation("c", "copper_ores"), MaterialColor.TERRACOTTA_ORANGE.col);
        c.put(new ResourceLocation("c", "lead_ores"), MaterialColor.TERRACOTTA_BLUE.col);
        c.put(new ResourceLocation("c", "silver_ores"), MaterialColor.COLOR_LIGHT_GRAY.col);
        c.put(new ResourceLocation("c", "nickel_ores"), MaterialColor.COLOR_LIGHT_BLUE.col);
        c.put(new ResourceLocation("c", "platinum_ores"), MaterialColor.TERRACOTTA_WHITE.col);
        //c.put(new ResourceLocation("forge", "ores/mithril"), MaterialColor.COLOR_PURPLE.col);
    });

    @WorldRestart
    @Comment("The colors for fluids used when rendering their result bounding box\n" +
             "by fluid name. See `blockColors` for format entries have to be in.")
    @KeyValueTypes(keyType = ResourceLocation.class, valueType = int.class,
            valueSerializer = @CustomSerializer(serializer = "toHexString", deserializer = "fromHexString"))
    public static Object2IntMap<ResourceLocation> fluidColors = new Object2IntOpenHashMap<>();

    @WorldRestart
    @Comment("The colors for fluids used when rendering their result bounding box\n" +
             "by fluid tag. See `blockColors` for format entries have to be in.")
    @KeyValueTypes(keyType = ResourceLocation.class, valueType = int.class,
            valueSerializer = @CustomSerializer(serializer = "toHexString", deserializer = "fromHexString"))
    public static Object2IntMap<ResourceLocation> fluidTagColors = Util.make(new Object2IntOpenHashMap<>(), c -> {
        c.put(FluidTags.WATER.location(), MaterialColor.WATER.col);
        c.put(FluidTags.LAVA.location(), MaterialColor.TERRACOTTA_ORANGE.col);
    });

    @SuppressWarnings("unused") // Referenced in annotations.
    public static String toHexString(final Object value) {
        return "0x" + Integer.toHexString((int) value);
    }

    @SuppressWarnings("unused") // Referenced in annotations.
    public static Object fromHexString(final String value) {
        return Integer.decode(value);
    }
}
