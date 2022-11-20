package li.cil.scannable.common.config.forge;

import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.Tags;

import java.util.HashSet;
import java.util.Set;

public final class CommonConfigImpl {
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
}
