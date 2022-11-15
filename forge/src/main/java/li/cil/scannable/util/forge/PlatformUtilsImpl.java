package li.cil.scannable.util.forge;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import li.cil.scannable.api.scanning.ScannerModule;
import li.cil.scannable.common.forge.capabilities.Capabilities;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.common.Tags;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public final class PlatformUtilsImpl {
    public static Optional<ScannerModule> getModule(final ItemStack stack) {
        if (Capabilities.SCANNER_MODULE_CAPABILITY == null) return Optional.empty();
        return stack.getCapability(Capabilities.SCANNER_MODULE_CAPABILITY).resolve();
    }

    public static Set<ResourceLocation> getDefaultCommonOreTags() {
        return Util.make(new HashSet<>(), c -> {
            c.add(Tags.Blocks.ORES_COAL.location());
            c.add(Tags.Blocks.ORES_IRON.location());
            c.add(Tags.Blocks.ORES_REDSTONE.location());
            c.add(Tags.Blocks.ORES_QUARTZ.location());
            c.add(new ResourceLocation("forge", "ores/copper"));
            c.add(new ResourceLocation("forge", "ores/tin"));
        });
    }

    public static TagKey<Block> getTopLevelOreTag() {
        return Tags.Blocks.ORES;
    }

    public static Object2IntMap<ResourceLocation> getDefaultBlockTagColors() {
        return Util.make(new Object2IntOpenHashMap<>(), c -> {
            // Minecraft
            c.put(Tags.Blocks.ORES_COAL.location(), MaterialColor.COLOR_GRAY.col);
            c.put(Tags.Blocks.ORES_IRON.location(), MaterialColor.COLOR_BROWN.col); // MaterialColor.IRON is also gray, so...
            c.put(Tags.Blocks.ORES_GOLD.location(), MaterialColor.GOLD.col);
            c.put(Tags.Blocks.ORES_LAPIS.location(), MaterialColor.LAPIS.col);
            c.put(Tags.Blocks.ORES_DIAMOND.location(), MaterialColor.DIAMOND.col);
            c.put(Tags.Blocks.ORES_REDSTONE.location(), MaterialColor.COLOR_RED.col);
            c.put(Tags.Blocks.ORES_EMERALD.location(), MaterialColor.EMERALD.col);
            c.put(Tags.Blocks.ORES_QUARTZ.location(), MaterialColor.QUARTZ.col);

            // Common modded ores
            c.put(new ResourceLocation("forge", "ores/tin"), MaterialColor.COLOR_CYAN.col);
            c.put(new ResourceLocation("forge", "ores/copper"), MaterialColor.TERRACOTTA_ORANGE.col);
            c.put(new ResourceLocation("forge", "ores/lead"), MaterialColor.TERRACOTTA_BLUE.col);
            c.put(new ResourceLocation("forge", "ores/silver"), MaterialColor.COLOR_LIGHT_GRAY.col);
            c.put(new ResourceLocation("forge", "ores/nickel"), MaterialColor.COLOR_LIGHT_BLUE.col);
            c.put(new ResourceLocation("forge", "ores/platinum"), MaterialColor.TERRACOTTA_WHITE.col);
            c.put(new ResourceLocation("forge", "ores/mithril"), MaterialColor.COLOR_PURPLE.col);
        });
    }
}
