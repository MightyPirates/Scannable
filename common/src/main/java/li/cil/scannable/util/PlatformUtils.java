package li.cil.scannable.util;

import dev.architectury.injectables.annotations.ExpectPlatform;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import li.cil.scannable.api.scanning.ScannerModule;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Contract;

import java.util.Optional;
import java.util.Set;

@SuppressWarnings("Contract")
public final class PlatformUtils {
    @ExpectPlatform
    @Contract("_ -> !null")
    public static Optional<ScannerModule> getModule(final ItemStack stack) {
        throw new AssertionError();
    }

    @ExpectPlatform
    @Contract("_ -> !null")
    public static Set<ResourceLocation> getDefaultCommonOreTags() {
        throw new AssertionError();
    }

    @ExpectPlatform
    @Contract("_ -> !null")
    public static TagKey<Block> getTopLevelOreTag() {
        throw new AssertionError();
    }

    @ExpectPlatform
    @Contract("_ -> !null")
    public static Object2IntMap<ResourceLocation> getDefaultBlockTagColors() {
        throw new AssertionError();
    }
}
