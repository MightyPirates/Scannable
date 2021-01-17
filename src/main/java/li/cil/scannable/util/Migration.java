package li.cil.scannable.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
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
            return font.getStringPropertyWidth(text);
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
            return world.getDimensionKey();
        }

        public static BlockPos findNearestStructure(final ServerWorld world, final Structure<?> structure, final BlockPos pos, final int radius, final boolean skipExistingChunks) {
            return world.func_241117_a_(structure, pos, radius, skipExistingChunks);
        }
    }

    public static final class RegistryKey {
        public static <T> net.minecraft.util.RegistryKey<T> getKey(final net.minecraft.util.RegistryKey<? extends Registry<T>> registryLocation, final ResourceLocation keyLocation) {
            return net.minecraft.util.RegistryKey.getOrCreateKey(registryLocation, keyLocation);
        }

        public static <T> ResourceLocation getResourceLocation(final net.minecraft.util.RegistryKey<T> key) {
            return key.getLocation();
        }
    }
}
