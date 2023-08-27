package li.cil.scannable.common.config.fabric;


import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.Set;

public final class CommonConfigImpl {
    public static Set<ResourceLocation> getDefaultCommonOreTags() {
        return Util.make(new HashSet<>(), c -> {
            c.add(new ResourceLocation("c", "coal_ores"));
            c.add(new ResourceLocation("c", "iron_ores"));
            c.add(new ResourceLocation("c", "redstone_ores"));
            c.add(new ResourceLocation("c", "quartz_ores"));
            c.add(new ResourceLocation("c", "copper_ores"));
            c.add(new ResourceLocation("c", "tin_ores"));
        });
    }

    public static Set<ResourceLocation> getDefaultChestsTags() {
        return Util.make(new HashSet<>(), c -> {
            c.add(ConventionalBlockTags.CHESTS.location());
            c.add(ConventionalBlockTags.WOODEN_BARRELS.location());
            c.add(ConventionalBlockTags.SHULKER_BOXES.location());
        });
    }
}


