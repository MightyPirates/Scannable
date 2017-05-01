package li.cil.scannable.client.scanning;

import li.cil.scannable.api.prefab.AbstractScanResult;
import li.cil.scannable.api.prefab.AbstractScanResultProvider;
import li.cil.scannable.api.scanning.ScanResult;
import li.cil.scannable.common.capabilities.CapabilityScanResultProvider;
import li.cil.scannable.common.config.Settings;
import li.cil.scannable.common.init.Items;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public final class ScanResultProviderOre extends AbstractScanResultProvider implements ICapabilityProvider {
    public static final ScanResultProviderOre INSTANCE = new ScanResultProviderOre();

    // --------------------------------------------------------------------- //

    private final Map<IBlockState, ItemStack> oresCommon = new HashMap<>();
    private final Map<IBlockState, ItemStack> oresRare = new HashMap<>();
    private boolean scanCommon, scanRare;
    private int x, y, z;
    private BlockPos min, max;
    private int blocksPerTick;
    private Map<BlockPos, ScanResultOre> resultClusters = new HashMap<>();

    // --------------------------------------------------------------------- //

    private ScanResultProviderOre() {
        buildOreCache();
    }

    // --------------------------------------------------------------------- //
    // ICapabilityProvider

    @Override
    public boolean hasCapability(@Nonnull final Capability<?> capability, @Nullable final EnumFacing facing) {
        return capability == CapabilityScanResultProvider.SCAN_RESULT_PROVIDER_CAPABILITY;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> T getCapability(@Nonnull final Capability<T> capability, @Nullable final EnumFacing facing) {
        if (capability == CapabilityScanResultProvider.SCAN_RESULT_PROVIDER_CAPABILITY) {
            return (T) this;
        }
        return null;
    }

    // --------------------------------------------------------------------- //
    // ScanResultProvider

    @Override
    public void initialize(final EntityPlayer player, final Collection<ItemStack> modules, final Vec3d center, final float radius, final int scanTicks) {
        super.initialize(player, modules, center, radius, scanTicks);

        scanCommon = false;
        scanRare = false;
        for (final ItemStack module : modules) {
            scanCommon |= module.getItem() == Items.moduleOreCommon;
            scanRare |= module.getItem() == Items.moduleOreRare;
        }

        min = new BlockPos(center).add(-radius, -radius, -radius);
        max = new BlockPos(center).add(radius, radius, radius);
        x = min.getX();
        y = min.getY() - 1; // -1 for initial moveNext.
        z = min.getZ();
        final BlockPos size = max.subtract(min);
        final int count = size.getX() * size.getY() * size.getZ();
        blocksPerTick = MathHelper.ceil(count / (float) scanTicks);
    }

    @Override
    public void computeScanResults(final Consumer<ScanResult> callback) {
        final World world = player.getEntityWorld();
        for (int i = 0; i < blocksPerTick; i++) {
            if (!moveNext(world)) {
                return;
            }

            if (center.squareDistanceTo(x + 0.5, y + 0.5, z + 0.5) > radius * radius) {
                continue;
            }

            final BlockPos pos = new BlockPos(x, y, z);
            final IBlockState state = world.getBlockState(pos);
            ItemStack stack = null;
            if (scanCommon) {
                stack = oresCommon.get(state);
            }
            if (stack == null && scanRare) {
                stack = oresRare.get(state);
            }
            if (stack != null) {
                if (!tryAddToCluster(pos, stack)) {
                    final ScanResultOre result = new ScanResultOre(pos, stack);
                    callback.accept(result);
                    resultClusters.put(pos, result);
                }
            }
        }
    }

    @Override
    public void render(final Entity entity, final Iterable<ScanResult> results, final float partialTicks) {
        final double posX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
        final double posY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
        final double posZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;

        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

        GlStateManager.pushMatrix();
        GlStateManager.translate(-posX, -posY, -posZ);

        GlStateManager.color(0.3f, 0.4f, 0.8f, 0.4f);

        final Tessellator t = Tessellator.getInstance();
        final VertexBuffer buffer = t.getBuffer();

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);

        for (final ScanResult result : results) {
            final ScanResultOre resultOre = (ScanResultOre) result;
            drawCube(resultOre.bounds.minX, resultOre.bounds.minY, resultOre.bounds.minZ,
                     resultOre.bounds.maxX, resultOre.bounds.maxY, resultOre.bounds.maxZ,
                     buffer);
        }

        t.draw();

        GlStateManager.popMatrix();

        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
    }

    @Override
    public void reset() {
        super.reset();
        x = y = z = 0;
        min = max = null;
        blocksPerTick = 0;
        resultClusters.clear();
    }

    // --------------------------------------------------------------------- //

    private boolean tryAddToCluster(final BlockPos pos, final ItemStack stack) {
        final BlockPos min = pos.add(-2, -2, -2);
        final BlockPos max = pos.add(2, 2, 2);
        for (int y = min.getY(); y <= max.getY(); y++) {
            for (int x = min.getX(); x <= max.getX(); x++) {
                for (int z = min.getZ(); z <= max.getZ(); z++) {
                    final BlockPos clusterPos = new BlockPos(x, y, z);
                    final ScanResultOre cluster = resultClusters.get(clusterPos);
                    if (cluster != null && cluster.add(pos, stack)) {
                        resultClusters.put(pos, cluster);
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean moveNext(final World world) {
        y++;
        if (y > max.getY() || y >= world.getHeight()) {
            y = min.getY();
            x++;
            if (x > max.getX()) {
                x = min.getX();
                z++;
                if (z > max.getZ()) {
                    blocksPerTick = 0;
                    return false;
                }
            }
        }
        return true;
    }

    private void buildOreCache() {
        final Set<String> oreNamesBlacklist = new HashSet<>(Arrays.asList(Settings.oresBlacklist));
        final Set<String> oreNamesCommon = new HashSet<>(Arrays.asList(Settings.oresCommon));
        final Set<String> oreNamesRare = new HashSet<>(Arrays.asList(Settings.oresRare));

        final Pattern pattern = Pattern.compile("^ore[A-Z].*$");
        for (final Block block : ForgeRegistries.BLOCKS.getValues()) {
            for (final IBlockState state : block.getBlockState().getValidStates()) {
                final ItemStack stack = new ItemStack(block, 1, block.damageDropped(state));
                if (!stack.isEmpty()) {
                    final int[] ids = OreDictionary.getOreIDs(stack);
                    boolean isRare = false;
                    boolean isCommon = false;
                    for (final int id : ids) {
                        final String name = OreDictionary.getOreName(id);
                        if (oreNamesBlacklist.contains(name)) {
                            isRare = false;
                            isCommon = false;
                            break;
                        }
                        if (oreNamesRare.contains(name)) {
                            isRare = true;
                        }
                        if (oreNamesCommon.contains(name) || pattern.matcher(name).matches()) {
                            isCommon = true;
                        }
                    }

                    if (isRare) {
                        oresRare.put(state, stack);
                    } else if (isCommon) {
                        oresCommon.put(state, stack);
                    }
                }
            }
        }
    }

    private class ScanResultOre extends AbstractScanResult {
        private AxisAlignedBB bounds;
        private final ItemStack stack;

        ScanResultOre(final BlockPos pos, final ItemStack stack) {
            bounds = new AxisAlignedBB(pos);
            this.stack = stack;
        }

        boolean add(final BlockPos pos, final ItemStack stack) {
            if (!ItemStack.areItemStacksEqual(this.stack, stack)) {
                return false;
            }

            bounds = bounds.union(new AxisAlignedBB(pos));
            return true;
        }

        @Nullable
        @Override
        public AxisAlignedBB getRenderBounds() {
            return bounds;
        }

        @Override
        public Vec3d getPosition() {
            return bounds.getCenter();
        }
    }
}
