package li.cil.scannable.client;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import li.cil.scannable.util.ConfigManager.*;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.common.Tags;
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
        c.put(Tags.Blocks.ORES_COAL.getName(), MaterialColor.COLOR_GRAY.col);
        c.put(Tags.Blocks.ORES_IRON.getName(), MaterialColor.COLOR_BROWN.col); // MaterialColor.IRON is also gray, so...
        c.put(Tags.Blocks.ORES_GOLD.getName(), MaterialColor.GOLD.col);
        c.put(Tags.Blocks.ORES_LAPIS.getName(), MaterialColor.LAPIS.col);
        c.put(Tags.Blocks.ORES_DIAMOND.getName(), MaterialColor.DIAMOND.col);
        c.put(Tags.Blocks.ORES_REDSTONE.getName(), MaterialColor.COLOR_RED.col);
        c.put(Tags.Blocks.ORES_EMERALD.getName(), MaterialColor.EMERALD.col);
        c.put(Tags.Blocks.ORES_QUARTZ.getName(), MaterialColor.QUARTZ.col);

        // Common modded ores
        c.put(new ResourceLocation("forge", "ores/tin"), MaterialColor.COLOR_CYAN.col);
        c.put(new ResourceLocation("forge", "ores/copper"), MaterialColor.TERRACOTTA_ORANGE.col);
        c.put(new ResourceLocation("forge", "ores/lead"), MaterialColor.TERRACOTTA_BLUE.col);
        c.put(new ResourceLocation("forge", "ores/silver"), MaterialColor.COLOR_LIGHT_GRAY.col);
        c.put(new ResourceLocation("forge", "ores/nickel"), MaterialColor.COLOR_LIGHT_BLUE.col);
        c.put(new ResourceLocation("forge", "ores/platinum"), MaterialColor.TERRACOTTA_WHITE.col);
        c.put(new ResourceLocation("forge", "ores/mithril"), MaterialColor.COLOR_PURPLE.col);
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
        c.put(FluidTags.WATER.getName(), MaterialColor.WATER.col);
        c.put(FluidTags.LAVA.getName(), MaterialColor.TERRACOTTA_ORANGE.col);
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
