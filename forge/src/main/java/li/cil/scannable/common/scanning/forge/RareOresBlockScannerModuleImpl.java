package li.cil.scannable.common.scanning.forge;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;

public final class RareOresBlockScannerModuleImpl {
    public static TagKey<Block> getTopLevelOreTag() {
        return Tags.Blocks.ORES;
    }
}
