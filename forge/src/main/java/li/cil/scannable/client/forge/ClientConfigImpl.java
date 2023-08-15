package li.cil.scannable.client.forge;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.common.Tags;

public final class ClientConfigImpl {
    public static Object2IntMap<ResourceLocation> getDefaultBlockTagColors() {
        return Util.make(new Object2IntOpenHashMap<>(), c -> {
            // Minecraft
            c.put(Tags.Blocks.ORES_COAL.location(), MapColor.COLOR_GRAY.col);
            c.put(Tags.Blocks.ORES_IRON.location(), MapColor.COLOR_BROWN.col); // MaterialColor.IRON is also gray, so...
            c.put(Tags.Blocks.ORES_GOLD.location(), MapColor.GOLD.col);
            c.put(Tags.Blocks.ORES_LAPIS.location(), MapColor.LAPIS.col);
            c.put(Tags.Blocks.ORES_DIAMOND.location(), MapColor.DIAMOND.col);
            c.put(Tags.Blocks.ORES_REDSTONE.location(), MapColor.COLOR_RED.col);
            c.put(Tags.Blocks.ORES_EMERALD.location(), MapColor.EMERALD.col);
            c.put(Tags.Blocks.ORES_QUARTZ.location(), MapColor.QUARTZ.col);

            // Common modded ores
            c.put(new ResourceLocation("forge", "ores/tin"), MapColor.COLOR_CYAN.col);
            c.put(new ResourceLocation("forge", "ores/copper"), MapColor.TERRACOTTA_ORANGE.col);
            c.put(new ResourceLocation("forge", "ores/lead"), MapColor.TERRACOTTA_BLUE.col);
            c.put(new ResourceLocation("forge", "ores/silver"), MapColor.COLOR_LIGHT_GRAY.col);
            c.put(new ResourceLocation("forge", "ores/nickel"), MapColor.COLOR_LIGHT_BLUE.col);
            c.put(new ResourceLocation("forge", "ores/platinum"), MapColor.TERRACOTTA_WHITE.col);
            c.put(new ResourceLocation("forge", "ores/mithril"), MapColor.COLOR_PURPLE.col);
        });
    }
}
