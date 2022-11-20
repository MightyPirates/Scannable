package li.cil.scannable.client.fabric;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.MaterialColor;

public final class ClientConfigImpl {
    public static Object2IntMap<ResourceLocation> getDefaultBlockTagColors() {
        return Util.make(new Object2IntOpenHashMap<>(), c -> {
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
        });
    }
}
