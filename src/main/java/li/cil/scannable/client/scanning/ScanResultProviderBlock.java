package li.cil.scannable.client.scanning;

import com.google.common.base.Strings;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import li.cil.scannable.api.API;
import li.cil.scannable.api.prefab.AbstractScanResultProvider;
import li.cil.scannable.api.scanning.ScanFilterBlock;
import li.cil.scannable.api.scanning.ScanResult;
import li.cil.scannable.api.scanning.ScannerModule;
import li.cil.scannable.api.scanning.ScannerModuleBlock;
import li.cil.scannable.client.shader.ScanResultShader;
import li.cil.scannable.common.capabilities.CapabilityScannerModule;
import li.cil.scannable.common.config.Settings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.*;
import net.minecraft.util.palette.PalettedContainer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public final class ScanResultProviderBlock extends AbstractScanResultProvider {
    public static final ScanResultProviderBlock INSTANCE = new ScanResultProviderBlock();

    // --------------------------------------------------------------------- //

    private static final int DEFAULT_COLOR = 0x4466CC;

    private final List<ScanFilterLayer> scanFilterLayers = new ArrayList<>();
    private ChunkPos minChunkPos, maxChunkPos;
    private int minChunkSectionIndex, maxChunkSectionIndex;
    private int currentChunkX, currentChunkSectionIndex, currentChunkZ;
    private int chunkSectionsPerTick;
    private final Map<BlockPos, BlockScanResult> resultClusters = new HashMap<>();

    // --------------------------------------------------------------------- //
    // ScanResultProvider

    @Override
    public void initialize(final PlayerEntity player, final Collection<ItemStack> modules, final Vec3d center, final float radius, final int scanTicks) {
        super.initialize(player, modules, center, radius, scanTicks);

        scanFilterLayers.clear();

        final IntObjectMap<List<ScanFilterBlock>> filterByRadius = new IntObjectHashMap<>();
        for (final ItemStack module : modules) {
            final LazyOptional<ScannerModule> capability = module.getCapability(CapabilityScannerModule.SCANNER_MODULE_CAPABILITY);
            capability
                    .filter(c -> c instanceof ScannerModuleBlock)
                    .ifPresent(c -> {
                        final ScannerModuleBlock m = (ScannerModuleBlock) c;
                        final Optional<ScanFilterBlock> filter = m.getFilter(module);
                        filter.ifPresent(f -> {
                            final int localRadius = (int) Math.ceil(m.adjustLocalRange(this.radius));
                            filterByRadius.computeIfAbsent(localRadius, r -> new ArrayList<>()).add(f);
                        });
                    });
        }

        final IntList scanFilterKeys = new IntArrayList();
        scanFilterKeys.addAll(filterByRadius.keySet());
        scanFilterKeys.sort((a, b) -> -Integer.compare(a, b));

        if (scanFilterKeys.size() > 0) {
            this.radius = scanFilterKeys.getInt(0);
            for (final int r : scanFilterKeys) {
                scanFilterLayers.add(new ScanFilterLayer(r, filterByRadius.get(r)));
            }

            final BlockPos minBlockPos = new BlockPos(center).add(-this.radius, -this.radius, -this.radius);
            final BlockPos maxBlockPos = new BlockPos(center).add(this.radius, this.radius, this.radius);
            minChunkPos = new ChunkPos(minBlockPos);
            maxChunkPos = new ChunkPos(maxBlockPos);
            minChunkSectionIndex = Math.max(minBlockPos.getY() >> 4, 0);
            maxChunkSectionIndex = Math.min(maxBlockPos.getY() >> 4, 15);

            currentChunkX = minChunkPos.x;
            currentChunkSectionIndex = -1; // -1 for initial moveNext.
            currentChunkZ = minChunkPos.z;

            final int chunkSectionCount = ((maxChunkPos.x - minChunkPos.x) + 1) * ((maxChunkPos.z - minChunkPos.z) + 1) * ((maxChunkSectionIndex - minChunkSectionIndex) + 1);
            chunkSectionsPerTick = MathHelper.ceil(chunkSectionCount / (float) scanTicks);
        }
    }

    @Override
    public void computeScanResults(final Consumer<ScanResult> callback) {
        final World world = player.getEntityWorld();
        for (int i = 0; i < chunkSectionsPerTick; i++) {
            if (!moveNext()) {
                return;
            }

            // Skip chunks outside our bounding sphere defined by the global scan radius.
            final double dx = Math.min(Math.abs((currentChunkX << 4) - center.x), Math.abs((currentChunkX << 4) + 15 - center.x));
            final double dz = Math.min(Math.abs((currentChunkZ << 4) - center.z), Math.abs((currentChunkZ << 4) + 15 - center.z));
            final double dy = Math.min(Math.abs((currentChunkSectionIndex << 4) - center.y), Math.abs((currentChunkSectionIndex << 4) + 15 - center.y));
            if (dx * dx + dy * dy + dz * dz > radius * radius) {
                continue;
            }

            final IChunk chunk = world.getChunk(currentChunkX, currentChunkZ, ChunkStatus.FULL, false);
            if (chunk == null) {
                continue;
            }

            final ChunkSection[] sections = chunk.getSections();
            assert sections.length == 16;

            final ChunkSection section = sections[currentChunkSectionIndex];
            if (section == null || section.isEmpty()) {
                continue;
            }

            final PalettedContainer<BlockState> data = section.getData();
            final BlockPos origin = chunk.getPos().asBlockPos().add(0, section.getYLocation(), 0);
            final int originX = origin.getX();
            final int originY = origin.getY();
            final int originZ = origin.getZ();
            for (int index = 0; index < 16 * 16 * 16; index++) {
                final BlockState state = data.get(index);
                if (Settings.shouldIgnore(state.getBlock())) {
                    continue;
                }

                final int x = index & 0xf;
                final int z = (index >> 4) & 0xf;
                final int y = (index >> 8) & 0xf;

                final int globalX = originX + x;
                final int globalY = originY + y;
                final int globalZ = originZ + z;

                final double squaredDistance = center.squareDistanceTo(globalX + 0.5, globalY + 0.5, globalZ + 0.5);

                outer:
                for (final ScanFilterLayer layer : scanFilterLayers) {
                    if (squaredDistance > layer.radius * layer.radius) {
                        break; // Filters radii only get smaller in the sorted filter list.
                    }

                    for (final ScanFilterBlock filter : layer.filters) {
                        if (filter.matches(state)) {
                            final BlockPos pos = new BlockPos(globalX, globalY, globalZ);
                            if (!tryAddToCluster(pos, state.getBlock())) {
                                final BlockScanResult result = new BlockScanResult(state.getBlock(), pos);
                                callback.accept(result);
                                resultClusters.put(pos, result);
                            }
                            break outer;
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean bakeResult(final IBlockReader world, final ScanResult result) {
        final BlockScanResult blockResult = (BlockScanResult) result;
        if (blockResult.isRoot()) {
            blockResult.bake(world);
            return true;
        }
        return false;
    }

    @Override
    public void render(final IRenderTypeBuffer renderTypeBuffer, final MatrixStack matrixStack, final Matrix4f projectionMatrix, final ActiveRenderInfo renderInfo, final float partialTicks, final List<ScanResult> results) {
        // Re-render hands into depth buffer to avoid rendering overlay on top of player hands.
        if (Minecraft.getInstance().gameRenderer.renderHand) {
            RenderSystem.colorMask(false, false, false, false);
            matrixStack.push();
            Minecraft.getInstance().gameRenderer.renderHand(matrixStack, renderInfo, partialTicks);
            matrixStack.pop();
            RenderSystem.colorMask(true, true, true, true);
        }

        ScanResultShader.setProjectionMatrix(projectionMatrix);
        ScanResultShader.setViewMatrix(matrixStack.getLast().getMatrix());

        final RenderType renderType = getBlockScanResultRenderLayer();
        renderType.setupRenderState();
        for (final ScanResult result : results) {
            final BlockScanResult blockResult = (BlockScanResult) result;
            final VertexBuffer vbo = blockResult.vbo;
            vbo.bindBuffer();
            DefaultVertexFormats.POSITION_COLOR_TEX.setupBufferState(0);
            vbo.draw(matrixStack.getLast().getMatrix(), GL11.GL_QUADS);
            VertexBuffer.unbindBuffer();
            DefaultVertexFormats.POSITION_COLOR_TEX.clearBufferState();
        }
        renderType.clearRenderState();

        final Vec3d lookVec = new Vec3d(renderInfo.getViewVector());
        final Vec3d viewerEyes = renderInfo.getProjectedView();
        final float yaw = renderInfo.getYaw();
        final float pitch = renderInfo.getPitch();
        final boolean showDistance = renderInfo.getRenderViewEntity().isSneaking();

        // Order results by distance to center of screen (deviation from look
        // vector) so that labels we're looking at are in front of others.
        results.sort(Comparator.comparing(result -> {
            final BlockScanResult blockResult = (BlockScanResult) result;
            final Vec3d resultPos = blockResult.getPosition();
            final Vec3d toResult = resultPos.subtract(viewerEyes);
            return lookVec.dotProduct(toResult.normalize());
        }));

        for (final ScanResult result : results) {
            final BlockScanResult blockResult = (BlockScanResult) result;

            final Vec3d resultPos = result.getPosition();
            final Vec3d toResult = resultPos.subtract(viewerEyes);
            final float lookDirDot = (float) lookVec.dotProduct(toResult.normalize());

            final Block block = blockResult.block;
            final ITextComponent label = block.getNameTextComponent();
            if (lookDirDot > 0.98f && !Strings.isNullOrEmpty(label.getString())) {
                final float distance = showDistance ? (float) resultPos.subtract(viewerEyes).length() : 0f;
                renderIconLabel(renderTypeBuffer, matrixStack, yaw, pitch, lookVec, viewerEyes, distance, resultPos, API.ICON_INFO, label);
            }
        }
    }

    @Override
    public void reset() {
        super.reset();
        scanFilterLayers.clear();
        minChunkPos = maxChunkPos = null;
        minChunkSectionIndex = maxChunkSectionIndex = 0;
        chunkSectionsPerTick = 0;
        currentChunkX = currentChunkSectionIndex = currentChunkZ = 0;
        resultClusters.clear();
    }

    // --------------------------------------------------------------------- //

    private static RenderType getBlockScanResultRenderLayer() {
        return RenderType.makeType("scan_result",
                DefaultVertexFormats.POSITION_COLOR_TEX,
                GL11.GL_QUADS,
                65536,
                RenderType.State.getBuilder()
                        .transparency(RenderState.LIGHTNING_TRANSPARENCY)
                        .writeMask(RenderState.COLOR_WRITE)
                        .cull(RenderState.CULL_DISABLED)
                        .texturing(new RenderState.TexturingState("shader",
                                ScanResultShader.INSTANCE::bind, ScanResultShader.INSTANCE::unbind))
                        .build(false));
    }

    private boolean tryAddToCluster(final BlockPos pos, final Block block) {
        final BlockPos min = pos.add(-1, -1, -1);
        final BlockPos max = pos.add(1, 1, 1);

        BlockScanResult root = null;
        for (int y = min.getY(); y <= max.getY(); y++) {
            for (int x = min.getX(); x <= max.getX(); x++) {
                for (int z = min.getZ(); z <= max.getZ(); z++) {
                    final BlockPos clusterPos = new BlockPos(x, y, z);
                    final BlockScanResult cluster = resultClusters.get(clusterPos);
                    if (cluster == null) {
                        continue;
                    }
                    if (block != cluster.block) {
                        continue;
                    }

                    if (root == null) {
                        root = cluster.getRoot();
                        root.add(pos);
                        resultClusters.put(pos, root);
                    } else {
                        cluster.setRoot(root);
                    }
                }
            }
        }

        return root != null;
    }

    private boolean moveNext() {
        currentChunkSectionIndex++;
        if (currentChunkSectionIndex > maxChunkSectionIndex || currentChunkSectionIndex >= 15) {
            currentChunkSectionIndex = minChunkSectionIndex;
            currentChunkX++;
            if (currentChunkX > maxChunkPos.x) {
                currentChunkX = minChunkPos.x;
                currentChunkZ++;
                if (currentChunkZ > maxChunkPos.z) {
                    chunkSectionsPerTick = 0;
                    return false;
                }
            }
        }
        return true;
    }

    private static final class ScanFilterLayer {
        public int radius;
        public List<ScanFilterBlock> filters;

        public ScanFilterLayer(final int radius, final List<ScanFilterBlock> filters) {
            this.radius = radius;
            this.filters = filters;
        }
    }

    // --------------------------------------------------------------------- //

    private static final class BlockScanResult implements ScanResult {
        private final Block block;
        private AxisAlignedBB bounds;
        @Nullable
        private BlockScanResult parent;
        private final Set<BlockPos> blocks;
        private int color;
        private VertexBuffer vbo;

        BlockScanResult(final Block block, final BlockPos pos) {
            this.block = block;
            bounds = new AxisAlignedBB(pos);
            blocks = new HashSet<>();
            blocks.add(pos);
        }

        void bake(final IBlockReader world) {
            final BlockState blockState = block.getDefaultState();

            color = blockState.getMaterialColor(world, new BlockPos(bounds.getCenter())).colorValue;
            if (color == 0) { // E.g. glass.
                color = DEFAULT_COLOR;
            }

            final IFluidState fluidState = blockState.getFluidState();
            if (!fluidState.isEmpty()) {
                if (Settings.fluidColors.containsKey(fluidState.getFluid())) {
                    color = Settings.fluidColors.getInt(fluidState.getFluid());
                } else {
                    Settings.fluidTagColors.forEach((k, v) -> {
                        if (k.contains(fluidState.getFluid())) {
                            color = v;
                        }
                    });
                }
            } else {
                if (Settings.blockColors.containsKey(blockState.getBlock())) {
                    color = Settings.blockColors.getInt(blockState.getBlock());
                } else {
                    Settings.blockTagColors.forEach((k, v) -> {
                        if (k.contains(blockState.getBlock())) {
                            color = v;
                        }
                    });
                }
            }

            final Tessellator tessellator = Tessellator.getInstance();
            final BufferBuilder buffer = tessellator.getBuffer();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);
            final MatrixStack matrixStack = new MatrixStack();
            render(buffer, matrixStack);
            buffer.finishDrawing();
            vbo = new VertexBuffer(DefaultVertexFormats.POSITION_COLOR_TEX);
            vbo.upload(buffer);
        }

        boolean isRoot() {
            return parent == null;
        }

        BlockScanResult getRoot() {
            if (parent != null) {
                return parent.getRoot();
            }
            return this;
        }

        void setRoot(final BlockScanResult root) {
            if (parent != null) {
                parent.setRoot(root);
                return;
            }
            if (root == this) {
                return;
            }

            root.bounds = root.bounds.union(bounds);
            root.blocks.addAll(blocks);
            blocks.clear();
            parent = root;
        }

        void add(final BlockPos pos) {
            assert parent == null : "Trying to add to non-root node.";
            bounds = bounds.union(new AxisAlignedBB(pos));
            blocks.add(pos);
        }

        void render(final IVertexBuilder buffer, final MatrixStack matrixStack) {
            final Matrix4f matrix = matrixStack.getLast().getMatrix();

            final float colorNormalizer = 1 / 255f;
            final float r = ((color >> 16) & 0xFF) * colorNormalizer;
            final float g = ((color >> 8) & 0xFF) * colorNormalizer;
            final float b = (color & 0xFF) * colorNormalizer;

            final float sizeUvX = (float) (1.0 / bounds.getXSize());
            final float sizeUvY = (float) (1.0 / bounds.getYSize());
            final float sizeUvZ = (float) (1.0 / bounds.getZSize());
            for (final BlockPos cell : blocks) {
                if (!blocks.contains(cell.add(-1, 0, 0))) {
                    final float x = cell.getX();
                    final float minY = cell.getY();
                    final float maxY = cell.getY() + 1;
                    final float minZ = cell.getZ();
                    final float maxZ = cell.getZ() + 1;
                    final float u0 = (minY - (float) bounds.minY) * sizeUvY;
                    final float u1 = u0 + sizeUvY;
                    final float v0 = (minZ - (float) bounds.minZ) * sizeUvZ;
                    final float v1 = v0 + sizeUvZ;
                    buffer.pos(matrix, x, minY, minZ).color(r, g, b, 0.8f).tex(u0, v0).endVertex();
                    buffer.pos(matrix, x, minY, maxZ).color(r, g, b, 0.8f).tex(u0, v1).endVertex();
                    buffer.pos(matrix, x, maxY, maxZ).color(r, g, b, 0.8f).tex(u1, v1).endVertex();
                    buffer.pos(matrix, x, maxY, minZ).color(r, g, b, 0.8f).tex(u1, v0).endVertex();
                }
                if (!blocks.contains(cell.add(1, 0, 0))) {
                    final float x = cell.getX() + 1;
                    final float minY = cell.getY();
                    final float maxY = cell.getY() + 1;
                    final float minZ = cell.getZ();
                    final float maxZ = cell.getZ() + 1;
                    final float u0 = (minY - (float) bounds.minY) * sizeUvY;
                    final float u1 = u0 + sizeUvY;
                    final float v0 = (minZ - (float) bounds.minZ) * sizeUvZ;
                    final float v1 = v0 + sizeUvZ;
                    buffer.pos(matrix, x, minY, minZ).color(r, g, b, 0.8f).tex(u0, v0).endVertex();
                    buffer.pos(matrix, x, maxY, minZ).color(r, g, b, 0.8f).tex(u1, v0).endVertex();
                    buffer.pos(matrix, x, maxY, maxZ).color(r, g, b, 0.8f).tex(u1, v1).endVertex();
                    buffer.pos(matrix, x, minY, maxZ).color(r, g, b, 0.8f).tex(u0, v1).endVertex();
                }
                if (!blocks.contains(cell.add(0, -1, 0))) {
                    final float y = cell.getY();
                    final float minX = cell.getX();
                    final float maxX = cell.getX() + 1;
                    final float minZ = cell.getZ();
                    final float maxZ = cell.getZ() + 1;
                    final float u0 = (minX - (float) bounds.minX) * sizeUvX;
                    final float u1 = u0 + sizeUvX;
                    final float v0 = (minZ - (float) bounds.minZ) * sizeUvZ;
                    final float v1 = v0 + sizeUvZ;
                    buffer.pos(matrix, minX, y, minZ).color(r, g, b, 0.7f).tex(u0, v0).endVertex();
                    buffer.pos(matrix, maxX, y, minZ).color(r, g, b, 0.7f).tex(u1, v0).endVertex();
                    buffer.pos(matrix, maxX, y, maxZ).color(r, g, b, 0.7f).tex(u1, v1).endVertex();
                    buffer.pos(matrix, minX, y, maxZ).color(r, g, b, 0.7f).tex(u0, v1).endVertex();
                }
                if (!blocks.contains(cell.add(0, 1, 0))) {
                    final float y = cell.getY() + 1;
                    final float minX = cell.getX();
                    final float maxX = cell.getX() + 1;
                    final float minZ = cell.getZ();
                    final float maxZ = cell.getZ() + 1;
                    final float u0 = (minX - (float) bounds.minX) * sizeUvX;
                    final float u1 = u0 + sizeUvX;
                    final float v0 = (minZ - (float) bounds.minZ) * sizeUvZ;
                    final float v1 = v0 + sizeUvZ;
                    buffer.pos(matrix, minX, y, minZ).color(r, g, b, 1.0f).tex(u0, v0).endVertex();
                    buffer.pos(matrix, minX, y, maxZ).color(r, g, b, 1.0f).tex(u0, v1).endVertex();
                    buffer.pos(matrix, maxX, y, maxZ).color(r, g, b, 1.0f).tex(u1, v1).endVertex();
                    buffer.pos(matrix, maxX, y, minZ).color(r, g, b, 1.0f).tex(u1, v0).endVertex();
                }
                if (!blocks.contains(cell.add(0, 0, -1))) {
                    final float z = cell.getZ();
                    final float minX = cell.getX();
                    final float maxX = cell.getX() + 1;
                    final float minY = cell.getY();
                    final float maxY = cell.getY() + 1;
                    final float u0 = (minX - (float) bounds.minX) * sizeUvX;
                    final float u1 = u0 + sizeUvX;
                    final float v0 = (minY - (float) bounds.minY) * sizeUvY;
                    final float v1 = v0 + sizeUvY;
                    buffer.pos(matrix, minX, minY, z).color(r, g, b, 0.9f).tex(u0, v0).endVertex();
                    buffer.pos(matrix, minX, maxY, z).color(r, g, b, 0.9f).tex(u0, v1).endVertex();
                    buffer.pos(matrix, maxX, maxY, z).color(r, g, b, 0.9f).tex(u1, v1).endVertex();
                    buffer.pos(matrix, maxX, minY, z).color(r, g, b, 0.9f).tex(u1, v0).endVertex();
                }
                if (!blocks.contains(cell.add(0, 0, 1))) {
                    final float z = cell.getZ() + 1;
                    final float minX = cell.getX();
                    final float maxX = cell.getX() + 1;
                    final float minY = cell.getY();
                    final float maxY = cell.getY() + 1;
                    final float u0 = (minX - (float) bounds.minX) * sizeUvX;
                    final float u1 = u0 + sizeUvX;
                    final float v0 = (minY - (float) bounds.minY) * sizeUvY;
                    final float v1 = v0 + sizeUvY;
                    buffer.pos(matrix, minX, minY, z).color(r, g, b, 0.9f).tex(u0, v0).endVertex();
                    buffer.pos(matrix, maxX, minY, z).color(r, g, b, 0.9f).tex(u1, v0).endVertex();
                    buffer.pos(matrix, maxX, maxY, z).color(r, g, b, 0.9f).tex(u1, v1).endVertex();
                    buffer.pos(matrix, minX, maxY, z).color(r, g, b, 0.9f).tex(u0, v1).endVertex();
                }
            }
        }

        // --------------------------------------------------------------------- //
        // ScanResult

        @Nullable
        @Override
        public AxisAlignedBB getRenderBounds() {
            return bounds;
        }

        @Override
        public Vec3d getPosition() {
            return bounds.getCenter();
        }

        @Override
        public void close() {
            if (vbo != null) {
                vbo.close();
                vbo = null;
            }
        }
    }

    // --------------------------------------------------------------------- //

    private ScanResultProviderBlock() {
    }
}
