package li.cil.scannable.client.scanning;

import com.google.common.base.Strings;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import li.cil.scannable.api.API;
import li.cil.scannable.api.prefab.AbstractScanResultProvider;
import li.cil.scannable.api.scanning.BlockScannerModule;
import li.cil.scannable.api.scanning.ScanResult;
import li.cil.scannable.api.scanning.ScanResultRenderContext;
import li.cil.scannable.api.scanning.ScannerModule;
import li.cil.scannable.client.ClientConfig;
import li.cil.scannable.client.shader.Shaders;
import li.cil.scannable.common.item.ScannerModuleItem;
import li.cil.scannable.common.scanning.filter.IgnoredBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Environment(EnvType.CLIENT)
public final class ScanResultProviderBlock extends AbstractScanResultProvider {
    private static final Logger LOGGER = LogManager.getLogger();

    // Sanity performance check. Maybe some day I'll do some research on how to
    // do the clustering more efficiently, but for now this is good enough. We
    // really only need this when scanning for stupid stuff like stone.
    private static final int MAX_RESULTS_PER_BLOCK = 8192;
    private static final int DEFAULT_COLOR = 0x4466CC;

    private final List<ScanFilterLayer> scanFilterLayers = new ArrayList<>();
    private final List<ChunkSectionPos> pendingChunkSections = new ArrayList<>();
    private int currentChunkSection, chunkSectionsPerTick;
    private final Map<Block, Map<BlockPos, BlockScanResult>> resultClusters = new HashMap<>();
    private final List<BlockScanResult> results = new ArrayList<>();

    private long renderStartTime;

    // --------------------------------------------------------------------- //
    // ScanResultProvider

    @Override
    public void initialize(final Player player, final Collection<ItemStack> modules, final Vec3 center, final float radius, final int scanTicks) {
        super.initialize(player, modules, center, radius, scanTicks);

        scanFilterLayers.clear();

        final IntObjectMap<List<Predicate<BlockState>>> filterByRadius = new IntObjectHashMap<>();
        for (final ItemStack stack : modules) {
            final Optional<ScannerModule> capability = ScannerModuleItem.getModule(stack);
            capability.ifPresent(module -> {
                if (module instanceof BlockScannerModule blockModule) {
                    final Predicate<BlockState> filter = blockModule.getFilter(stack);
                    final int localRadius = (int) Math.ceil(blockModule.adjustLocalRange(this.radius));
                    filterByRadius.computeIfAbsent(localRadius, r -> new ArrayList<>()).add(filter);
                }
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

            final BlockPos minBlockPos = new BlockPos(center).offset(-this.radius, -this.radius, -this.radius);
            final BlockPos maxBlockPos = new BlockPos(center).offset(this.radius, this.radius, this.radius);
            final ChunkPos minChunkPos = new ChunkPos(minBlockPos);
            final ChunkPos maxChunkPos = new ChunkPos(maxBlockPos);

            final int minChunkSectionIndex = Math.max(player.getLevel().getSectionIndex(minBlockPos.getY()), 0);
            final int maxChunkSectionIndex = Math.min(player.getLevel().getSectionIndex(maxBlockPos.getY()), player.getLevel().getSectionsCount() - 1);

            for (int chunkSectionIndex = minChunkSectionIndex; chunkSectionIndex <= maxChunkSectionIndex; chunkSectionIndex++) {
                for (int chunkZ = minChunkPos.z; chunkZ <= maxChunkPos.z; chunkZ++) {
                    for (int chunkX = minChunkPos.x; chunkX <= maxChunkPos.x; chunkX++) {
                        final ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);
                        final int chunkY = player.getLevel().getSectionYFromSectionIndex(chunkSectionIndex);

                        final double dx = Math.min(
                            Math.abs(chunkPos.getMinBlockX() - center.x),
                            Math.abs(chunkPos.getMaxBlockX() - center.x));
                        final double dz = Math.min(
                            Math.abs(chunkPos.getMinBlockZ() - center.z),
                            Math.abs(chunkPos.getMaxBlockZ() - center.z));
                        final double dy = Math.min(
                            Math.abs(SectionPos.sectionToBlockCoord(chunkY, 0) - center.y),
                            Math.abs(SectionPos.sectionToBlockCoord(chunkY, SectionPos.SECTION_MAX_INDEX) - center.y));
                        final double squareDistToCenter = dx * dx + dy * dy + dz * dz;

                        if (squareDistToCenter > radius * radius) {
                            continue;
                        }

                        pendingChunkSections.add(new ChunkSectionPos(chunkX, chunkZ, chunkSectionIndex, squareDistToCenter));
                    }
                }
            }

            pendingChunkSections.sort(Comparator.comparingDouble(p -> p.squareDistToCenter));

            chunkSectionsPerTick = Mth.ceil(pendingChunkSections.size() / (float) scanTicks);
            this.currentChunkSection = 0;
        }
    }

    @Override
    public void computeScanResults() {
        final Level level = player.level;
        for (int i = 0; i < chunkSectionsPerTick; i++) {
            if (currentChunkSection >= pendingChunkSections.size()) {
                return;
            }

            final ChunkSectionPos chunkSectionPos = pendingChunkSections.get(currentChunkSection);
            currentChunkSection++;

            final int chunkX = chunkSectionPos.chunkX;
            final int chunkZ = chunkSectionPos.chunkZ;
            final int chunkSectionIndex = chunkSectionPos.chunkSectionIndex;

            final ChunkAccess chunk = level.getChunk(chunkX, chunkZ, ChunkStatus.FULL, false);
            if (chunk == null) {
                continue;
            }

            final LevelChunkSection[] sections = chunk.getSections();
            final LevelChunkSection section = sections[chunkSectionIndex];
            if (section == null || section.hasOnlyAir()) {
                continue;
            }

            final PalettedContainer<BlockState> palette = section.getStates();
            final BlockPos origin = chunk.getPos().getWorldPosition().offset(0, section.bottomBlockY(), 0);
            final int originX = origin.getX();
            final int originY = origin.getY();
            final int originZ = origin.getZ();
            for (int index = 0; index < 16 * 16 * 16; index++) {
                final BlockState state = palette.get(index);
                final Block block = state.getBlock();
                final Map<BlockPos, BlockScanResult> clusters = resultClusters.computeIfAbsent(block, b -> new HashMap<>());
                if (clusters.size() > MAX_RESULTS_PER_BLOCK) {
                    continue;
                }

                if (IgnoredBlocks.contains(state)) {
                    continue;
                }

                final int x = index & 0xf;
                final int z = (index >> 4) & 0xf;
                final int y = (index >> 8) & 0xf;

                final int globalX = originX + x;
                final int globalY = originY + y;
                final int globalZ = originZ + z;

                final double squaredDistance = center.distanceToSqr(globalX + 0.5, globalY + 0.5, globalZ + 0.5);

                outer:
                for (final ScanFilterLayer layer : scanFilterLayers) {
                    if (squaredDistance > layer.radius * layer.radius) {
                        break; // Filters radii only get smaller in the sorted filter list.
                    }

                    for (final Predicate<BlockState> filter : layer.filters) {
                        if (filter.test(state)) {
                            final BlockPos pos = new BlockPos(globalX, globalY, globalZ);
                            if (!tryAddToCluster(clusters, pos)) {
                                final BlockScanResult result = new BlockScanResult(state.getBlock(), pos);
                                clusters.put(pos, result);
                                results.add(result);
                            }
                            break outer;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void collectScanResults(final BlockGetter level, final Consumer<ScanResult> callback) {
        for (final BlockScanResult result : results) {
            if (result.isRoot()) {
                result.bake(level);
                callback.accept(result);
            }
        }

        renderStartTime = System.currentTimeMillis();
    }

    @Override
    public void render(final ScanResultRenderContext context, final MultiBufferSource bufferSource, final PoseStack poseStack, final Camera renderInfo, final float partialTicks, final List<ScanResult> results) {
        switch (context) {
            case WORLD -> renderBlocks(poseStack, renderInfo, partialTicks, results);
            case GUI -> renderBlockIcons(bufferSource, poseStack, renderInfo, results);
        }
    }

    @Override
    public void reset() {
        super.reset();
        scanFilterLayers.clear();
        currentChunkSection = chunkSectionsPerTick = 0;
        pendingChunkSections.clear();
        resultClusters.clear();
        results.clear();
    }

    // --------------------------------------------------------------------- //

    public static RenderType getBlockScanResultRenderLayer() {
        return RenderType.create("scan_result",
            DefaultVertexFormat.POSITION_COLOR_TEX,
            VertexFormat.Mode.QUADS,
            65536,
            false,
            false,
            RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(Shaders::getScanResultShader))
                .setTransparencyState(RenderStateShard.LIGHTNING_TRANSPARENCY)
                .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                .setCullState(RenderStateShard.NO_CULL)
                .setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
                .createCompositeState(false));
    }

    private void renderBlocks(final PoseStack poseStack, final Camera renderInfo, final float partialTicks, final List<ScanResult> results) {
        final ShaderInstance shader = Shaders.getScanResultShader();
        if (shader == null) {
            return;
        }

        // Re-render hands into depth buffer to avoid rendering overlay on top of player hands.
        if (Minecraft.getInstance().gameRenderer.renderHand) {
            final Matrix4f oldProjectionMatrix = RenderSystem.getProjectionMatrix();
            RenderSystem.colorMask(false, false, false, false);
            poseStack.pushPose();
            try {
                Minecraft.getInstance().gameRenderer.renderItemInHand(poseStack, renderInfo, partialTicks);
            } catch (final Throwable e) {
                LOGGER.catching(e);
            }
            poseStack.popPose();
            RenderSystem.colorMask(true, true, true, true);
            RenderSystem.setProjectionMatrix(oldProjectionMatrix);
        }

        shader.safeGetUniform("time").set((System.currentTimeMillis() - renderStartTime) / 1000.0f);

        final RenderType renderType = getBlockScanResultRenderLayer();
        renderType.setupRenderState();
        for (final ScanResult result : results) {
            final BlockScanResult blockResult = (BlockScanResult) result;
            final VertexBuffer vbo = blockResult.vbo;
            vbo.bind();
            vbo.drawWithShader(poseStack.last().pose(), RenderSystem.getProjectionMatrix(), shader);
            VertexBuffer.unbind();
        }
        renderType.clearRenderState();
    }

    private void renderBlockIcons(final MultiBufferSource bufferSource, final PoseStack poseStack, final Camera renderInfo, final List<ScanResult> results) {
        final Vec3 lookVec = new Vec3(renderInfo.getLookVector());
        final Vec3 viewerEyes = renderInfo.getPosition();
        final float yaw = renderInfo.getYRot();
        final float pitch = renderInfo.getXRot();
        final boolean showDistance = renderInfo.getEntity().isShiftKeyDown();

        // Order results by distance to center of screen (deviation from look
        // vector) so that labels we're looking at are in front of others.
        results.sort(Comparator.comparing(result -> {
            final BlockScanResult blockResult = (BlockScanResult) result;
            final Vec3 resultPos = blockResult.getPosition();
            final Vec3 toResult = resultPos.subtract(viewerEyes);
            return lookVec.dot(toResult.normalize());
        }));

        for (final ScanResult result : results) {
            final BlockScanResult blockResult = (BlockScanResult) result;

            final Vec3 resultPos = result.getPosition();
            final Vec3 toResult = resultPos.subtract(viewerEyes);
            final float lookDirDot = (float) lookVec.dot(toResult.normalize());

            final Block block = blockResult.block;
            final Component label = block.getName();
            if (lookDirDot > 0.98f && !Strings.isNullOrEmpty(label.getString())) {
                final float distance = showDistance ? (float) resultPos.subtract(viewerEyes).length() : 0f;
                renderIconLabel(bufferSource, poseStack, yaw, pitch, lookVec, viewerEyes, distance, resultPos, API.ICON_INFO, label);
            }
        }
    }

    private boolean tryAddToCluster(final Map<BlockPos, BlockScanResult> clusters, final BlockPos pos) {
        BlockScanResult root = null;
        root = tryAddToCluster(clusters, pos, pos.east(), root);
        root = tryAddToCluster(clusters, pos, pos.west(), root);
        root = tryAddToCluster(clusters, pos, pos.north(), root);
        root = tryAddToCluster(clusters, pos, pos.south(), root);
        root = tryAddToCluster(clusters, pos, pos.above(), root);
        root = tryAddToCluster(clusters, pos, pos.below(), root);
        return root != null;
    }

    @Nullable
    private BlockScanResult tryAddToCluster(final Map<BlockPos, BlockScanResult> clusters, final BlockPos pos, final BlockPos clusterPos, @Nullable BlockScanResult root) {
        final BlockScanResult cluster = clusters.get(clusterPos);
        if (cluster == null) {
            return root;
        }

        if (root == null) {
            root = cluster.getRoot();
            root.add(pos);
            clusters.put(pos, root);
        } else {
            cluster.getRoot().setRoot(root);
        }

        return root;
    }

    private record ScanFilterLayer(int radius, List<Predicate<BlockState>> filters) {
    }

    private record ChunkSectionPos(int chunkX, int chunkZ, int chunkSectionIndex, double squareDistToCenter) {
    }

    // --------------------------------------------------------------------- //

    private static final class BlockScanResult implements ScanResult {
        private final Block block;
        private AABB bounds;
        @Nullable private BlockScanResult parent;
        private final Set<BlockPos> blocks;
        private int color;
        private VertexBuffer vbo;

        BlockScanResult(final Block block, final BlockPos pos) {
            this.block = block;
            bounds = new AABB(pos);
            blocks = new HashSet<>();
            blocks.add(pos);
        }

        void bake(final BlockGetter level) {
            final BlockState blockState = block.defaultBlockState();

            color = blockState.getMapColor(level, new BlockPos(bounds.getCenter())).col;

            final FluidState fluidState = blockState.getFluidState();
            if (!fluidState.isEmpty()) {
                if (ClientConfig.fluidColors.containsKey(Registry.FLUID.getKey(fluidState.getType()))) {
                    color = ClientConfig.fluidColors.getInt(Registry.FLUID.getKey(fluidState.getType()));
                } else {
                    ClientConfig.fluidTagColors.forEach((k, v) -> {
                        final TagKey<Fluid> tag = TagKey.create(Registry.FLUID_REGISTRY, k);
                        if (fluidState.is(tag)) {
                            color = v;
                        }
                    });
                }
            } else {
                if (ClientConfig.blockColors.containsKey(Registry.BLOCK.getKey(blockState.getBlock()))) {
                    color = ClientConfig.blockColors.getInt(Registry.BLOCK.getKey(blockState.getBlock()));
                } else {
                    ClientConfig.blockTagColors.forEach((k, v) -> {
                        final TagKey<Block> tag = TagKey.create(Registry.BLOCK_REGISTRY, k);
                        if (blockState.is(tag)) {
                            color = v;
                        }
                    });
                }
            }

            if (color == 0) { // E.g. glass.
                color = DEFAULT_COLOR;
            }

            final BufferBuilder buffer = Tesselator.getInstance().getBuilder();
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            render(buffer, new PoseStack());
            vbo = new VertexBuffer();
            vbo.bind();
            vbo.upload(buffer.end());
            VertexBuffer.unbind();
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
            if (root == this) {
                return;
            }

            assert parent == null;

            root.bounds = root.bounds.minmax(bounds);
            root.blocks.addAll(blocks);
            blocks.clear();
            parent = root;
        }

        void add(final BlockPos pos) {
            assert parent == null : "Trying to add to non-root node.";
            bounds = bounds.minmax(new AABB(pos));
            blocks.add(pos);
        }

        void render(final VertexConsumer buffer, final PoseStack poseStack) {
            final Matrix4f matrix = poseStack.last().pose();

            final float colorNormalizer = 1 / 255f;
            final float r = ((color >> 16) & 0xFF) * colorNormalizer;
            final float g = ((color >> 8) & 0xFF) * colorNormalizer;
            final float b = (color & 0xFF) * colorNormalizer;

            final float sizeUvX = (float) (1.0 / bounds.getXsize());
            final float sizeUvY = (float) (1.0 / bounds.getYsize());
            final float sizeUvZ = (float) (1.0 / bounds.getZsize());
            for (final BlockPos cell : blocks) {
                if (!blocks.contains(cell.offset(-1, 0, 0))) {
                    final float x = cell.getX();
                    final float minY = cell.getY();
                    final float maxY = cell.getY() + 1;
                    final float minZ = cell.getZ();
                    final float maxZ = cell.getZ() + 1;
                    final float u0 = (minY - (float) bounds.minY) * sizeUvY;
                    final float u1 = u0 + sizeUvY;
                    final float v0 = (minZ - (float) bounds.minZ) * sizeUvZ;
                    final float v1 = v0 + sizeUvZ;
                    buffer.vertex(matrix, x, minY, minZ).uv(u0, v0).color(r, g, b, 0.8f).endVertex();
                    buffer.vertex(matrix, x, minY, maxZ).uv(u0, v1).color(r, g, b, 0.8f).endVertex();
                    buffer.vertex(matrix, x, maxY, maxZ).uv(u1, v1).color(r, g, b, 0.8f).endVertex();
                    buffer.vertex(matrix, x, maxY, minZ).uv(u1, v0).color(r, g, b, 0.8f).endVertex();
                }
                if (!blocks.contains(cell.offset(1, 0, 0))) {
                    final float x = cell.getX() + 1;
                    final float minY = cell.getY();
                    final float maxY = cell.getY() + 1;
                    final float minZ = cell.getZ();
                    final float maxZ = cell.getZ() + 1;
                    final float u0 = (minY - (float) bounds.minY) * sizeUvY;
                    final float u1 = u0 + sizeUvY;
                    final float v0 = (minZ - (float) bounds.minZ) * sizeUvZ;
                    final float v1 = v0 + sizeUvZ;
                    buffer.vertex(matrix, x, minY, minZ).uv(u0, v0).color(r, g, b, 0.8f).endVertex();
                    buffer.vertex(matrix, x, maxY, minZ).uv(u1, v0).color(r, g, b, 0.8f).endVertex();
                    buffer.vertex(matrix, x, maxY, maxZ).uv(u1, v1).color(r, g, b, 0.8f).endVertex();
                    buffer.vertex(matrix, x, minY, maxZ).uv(u0, v1).color(r, g, b, 0.8f).endVertex();
                }
                if (!blocks.contains(cell.offset(0, -1, 0))) {
                    final float y = cell.getY();
                    final float minX = cell.getX();
                    final float maxX = cell.getX() + 1;
                    final float minZ = cell.getZ();
                    final float maxZ = cell.getZ() + 1;
                    final float u0 = (minX - (float) bounds.minX) * sizeUvX;
                    final float u1 = u0 + sizeUvX;
                    final float v0 = (minZ - (float) bounds.minZ) * sizeUvZ;
                    final float v1 = v0 + sizeUvZ;
                    buffer.vertex(matrix, minX, y, minZ).uv(u0, v0).color(r, g, b, 0.7f).endVertex();
                    buffer.vertex(matrix, maxX, y, minZ).uv(u1, v0).color(r, g, b, 0.7f).endVertex();
                    buffer.vertex(matrix, maxX, y, maxZ).uv(u1, v1).color(r, g, b, 0.7f).endVertex();
                    buffer.vertex(matrix, minX, y, maxZ).uv(u0, v1).color(r, g, b, 0.7f).endVertex();
                }
                if (!blocks.contains(cell.offset(0, 1, 0))) {
                    final float y = cell.getY() + 1;
                    final float minX = cell.getX();
                    final float maxX = cell.getX() + 1;
                    final float minZ = cell.getZ();
                    final float maxZ = cell.getZ() + 1;
                    final float u0 = (minX - (float) bounds.minX) * sizeUvX;
                    final float u1 = u0 + sizeUvX;
                    final float v0 = (minZ - (float) bounds.minZ) * sizeUvZ;
                    final float v1 = v0 + sizeUvZ;
                    buffer.vertex(matrix, minX, y, minZ).uv(u0, v0).color(r, g, b, 1.0f).endVertex();
                    buffer.vertex(matrix, minX, y, maxZ).uv(u0, v1).color(r, g, b, 1.0f).endVertex();
                    buffer.vertex(matrix, maxX, y, maxZ).uv(u1, v1).color(r, g, b, 1.0f).endVertex();
                    buffer.vertex(matrix, maxX, y, minZ).uv(u1, v0).color(r, g, b, 1.0f).endVertex();
                }
                if (!blocks.contains(cell.offset(0, 0, -1))) {
                    final float z = cell.getZ();
                    final float minX = cell.getX();
                    final float maxX = cell.getX() + 1;
                    final float minY = cell.getY();
                    final float maxY = cell.getY() + 1;
                    final float u0 = (minX - (float) bounds.minX) * sizeUvX;
                    final float u1 = u0 + sizeUvX;
                    final float v0 = (minY - (float) bounds.minY) * sizeUvY;
                    final float v1 = v0 + sizeUvY;
                    buffer.vertex(matrix, minX, minY, z).uv(u0, v0).color(r, g, b, 0.9f).endVertex();
                    buffer.vertex(matrix, minX, maxY, z).uv(u0, v1).color(r, g, b, 0.9f).endVertex();
                    buffer.vertex(matrix, maxX, maxY, z).uv(u1, v1).color(r, g, b, 0.9f).endVertex();
                    buffer.vertex(matrix, maxX, minY, z).uv(u1, v0).color(r, g, b, 0.9f).endVertex();
                }
                if (!blocks.contains(cell.offset(0, 0, 1))) {
                    final float z = cell.getZ() + 1;
                    final float minX = cell.getX();
                    final float maxX = cell.getX() + 1;
                    final float minY = cell.getY();
                    final float maxY = cell.getY() + 1;
                    final float u0 = (minX - (float) bounds.minX) * sizeUvX;
                    final float u1 = u0 + sizeUvX;
                    final float v0 = (minY - (float) bounds.minY) * sizeUvY;
                    final float v1 = v0 + sizeUvY;
                    buffer.vertex(matrix, minX, minY, z).uv(u0, v0).color(r, g, b, 0.9f).endVertex();
                    buffer.vertex(matrix, maxX, minY, z).uv(u1, v0).color(r, g, b, 0.9f).endVertex();
                    buffer.vertex(matrix, maxX, maxY, z).uv(u1, v1).color(r, g, b, 0.9f).endVertex();
                    buffer.vertex(matrix, minX, maxY, z).uv(u0, v1).color(r, g, b, 0.9f).endVertex();
                }
            }
        }

        // --------------------------------------------------------------------- //
        // ScanResult

        @Nullable
        @Override
        public AABB getRenderBounds() {
            return bounds;
        }

        @Override
        public Vec3 getPosition() {
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
}
