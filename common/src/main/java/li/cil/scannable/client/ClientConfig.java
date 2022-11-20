package li.cil.scannable.client;

import dev.architectury.injectables.annotations.ExpectPlatform;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import li.cil.scannable.util.config.*;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.material.MaterialColor;

@Type(ConfigType.CLIENT)
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
    public static Object2IntMap<ResourceLocation> blockTagColors = getDefaultBlockTagColors();

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

    @ExpectPlatform
    private static Object2IntMap<ResourceLocation> getDefaultBlockTagColors() {
        throw new AssertionError();
    }
}
