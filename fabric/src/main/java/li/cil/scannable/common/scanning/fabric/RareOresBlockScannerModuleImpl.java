package li.cil.scannable.common.scanning.fabric;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public final class RareOresBlockScannerModuleImpl {
    public static TagKey<Block> getTopLevelOreTag() {
        return TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation("c", "ores"));
    }
}
