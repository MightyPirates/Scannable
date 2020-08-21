package li.cil.scannable.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.server.ServerWorld;

public final class Migration {
    public static final class FontRenderer {
        public static void drawString(final net.minecraft.client.gui.FontRenderer font, final MatrixStack matrixStack, final ITextComponent text, final float x, final float y, final int color) {
            font.func_243248_b(matrixStack, text, x, y, color);
        }

        public static int getStringWidth(final net.minecraft.client.gui.FontRenderer font, final ITextProperties text) {
            return font.func_238414_a_(text);
        }

        public static void renderString(final net.minecraft.client.gui.FontRenderer font, final ITextComponent text, final float x, final float y, final int color, final boolean dropShadow, final Matrix4f matrix, final IRenderTypeBuffer buffer, final boolean transparent, final int backgroundColor, final int packedLight) {
            font.func_243247_a(text, x, y, color, dropShadow, matrix, buffer, transparent, backgroundColor, packedLight);
        }

        public static void drawStringWithShadow(final net.minecraft.client.gui.FontRenderer font, final MatrixStack matrixStack, final ITextComponent text, final float x, final float y, final int color) {
            font.func_238407_a_(matrixStack, text.func_241878_f(), x, y, color);
        }
    }

    public static final class World {
        public static net.minecraft.util.RegistryKey<net.minecraft.world.World> getDimension(final net.minecraft.world.World world) {
            return world.func_234923_W_();
        }

        public static BlockPos findNearestStructure(final ServerWorld world, final Structure<?> structure, final BlockPos pos, final int radius, final boolean skipExistingChunks) {
            return world.func_241117_a_(structure, pos, radius, skipExistingChunks);
        }
    }

    public static final class RegistryKey {
        public static <T> net.minecraft.util.RegistryKey<T> getKey(final net.minecraft.util.RegistryKey<? extends Registry<T>> registryLocation, final ResourceLocation keyLocation) {
            return net.minecraft.util.RegistryKey.func_240903_a_(registryLocation, keyLocation);
        }

        public static <T> ResourceLocation getResourceLocation(final net.minecraft.util.RegistryKey<T> key) {
            return key.func_240901_a_();
        }
    }

    public static final class BlockTags {
        public static ITag.INamedTag<Block> getOrCreateTag(final ResourceLocation location) {
            for (final ITag.INamedTag<Block> tag : net.minecraft.tags.BlockTags.func_242174_b()) {
                if (tag.getName().equals(location)) {
                    return tag;
                }
            }

            return net.minecraft.tags.BlockTags.createOptional(location);
        }
    }

    public static final class FluidTags {
        public static ITag.INamedTag<Fluid> getOrCreateTag(final ResourceLocation location) {
            for (final ITag.INamedTag<Fluid> tag : net.minecraft.tags.FluidTags.func_241280_c_()) {
                if (tag.getName().equals(location)) {
                    return tag;
                }
            }

            return net.minecraft.tags.FluidTags.createOptional(location);
        }
    }

    public static final class ItemTags {
        public static ITag.INamedTag<Item> getOrCreateTag(final ResourceLocation location) {
            for (final ITag.INamedTag<Item> tag : net.minecraft.tags.ItemTags.func_242177_b()) {
                if (tag.getName().equals(location)) {
                    return tag;
                }
            }

            return net.minecraft.tags.ItemTags.createOptional(location);
        }
    }
}
