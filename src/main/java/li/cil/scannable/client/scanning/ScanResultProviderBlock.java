package li.cil.scannable.client.scanning;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import li.cil.scannable.api.prefab.AbstractScanResultProvider;
import li.cil.scannable.api.scanning.ScanResult;
import li.cil.scannable.common.Scannable;
import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.config.Settings;
import li.cil.scannable.common.init.Items;
import li.cil.scannable.common.item.ItemScannerModuleBlockConfigurable;
import li.cil.scannable.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ScanResultProviderBlock extends AbstractScanResultProvider {
    public static final ScanResultProviderBlock INSTANCE = new ScanResultProviderBlock();

    // --------------------------------------------------------------------- //

    private static final int DEFAULT_COLOR = 0x4466CC;
    private static final float BASE_ALPHA = 0.25f;
    private static final float MIN_ALPHA = 0.13f; // Slightly > 0.1f/0.8f
    private static final float STATE_SCANNED_ALPHA = 0.7f;
    private static final Pattern STATE_DESC_PATTERN = Pattern.compile("(?<name>[^\\[]+)(?:\\[(?<properties>(?:[^,=\\]]+)=(?:[^,=\\]]+)(?:,(?:[^,=\\]]+)=(?:[^,=\\]]+))*)])?");

    private final TIntIntMap blockColors = new TIntIntHashMap();
    private final BitSet oresCommon = new BitSet();
    private final BitSet oresRare = new BitSet();
    private final BitSet fluids = new BitSet();
    private boolean scanCommon, scanRare, scanFluids;
    private final List<ScanFilter> scanFilters = new ArrayList<>();
    private float sqRadius, sqOreRadius;
    private BlockPos min, max;
    private int blocksPerTick;
    private int x, y, z;
    private Map<BlockPos, ScanResultOre> resultClusters = new HashMap<>();
    private List<ScanResultOre> nonCulledResults = new ArrayList<>();

    // --------------------------------------------------------------------- //

    private ScanResultProviderBlock() {
    }

    @SideOnly(Side.CLIENT)
    public void rebuildOreCache() {
        blockColors.clear();
        oresCommon.clear();
        oresRare.clear();
        fluids.clear();

        buildOreCache();
    }

    // --------------------------------------------------------------------- //
    // ScanResultProvider

    @Override
    public int getEnergyCost(final EntityPlayer player, final ItemStack module) {
        if (Items.isModuleOreCommon(module)) {
            return Settings.getEnergyCostModuleOreCommon();
        }
        if (Items.isModuleOreRare(module)) {
            return Settings.getEnergyCostModuleOreRare();
        }
        if (Items.isModuleBlock(module)) {
            return Settings.getEnergyCostModuleBlock();
        }
        if (Items.isModuleFluid(module)) {
            return Settings.getEnergyCostModuleFluid();
        }

        throw new IllegalArgumentException(String.format("Module not supported by this provider: %s", module));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void initialize(final EntityPlayer player, final Collection<ItemStack> modules, final Vec3d center, final float radius, final int scanTicks) {
        super.initialize(player, modules, center, computeRadius(modules, radius), scanTicks);

        scanCommon = false;
        scanRare = false;
        scanFluids = false;
        scanFilters.clear();
        for (final ItemStack module : modules) {
            scanCommon |= Items.isModuleOreCommon(module);
            scanRare |= Items.isModuleOreRare(module);
            scanFluids |= Items.isModuleFluid(module);
            if (Items.isModuleBlock(module)) {
                final IBlockState state = ItemScannerModuleBlockConfigurable.getBlockState(module);
                if (state != null) {
                    scanFilters.add(new ScanFilter(state));
                }
            }
        }

        sqRadius = this.radius * this.radius;
        sqOreRadius = radius * Constants.MODULE_ORE_RADIUS_MULTIPLIER;
        sqOreRadius *= sqOreRadius;
        min = new BlockPos(center).add(-this.radius, -this.radius, -this.radius);
        max = new BlockPos(center).add(this.radius, this.radius, this.radius);
        x = min.getX();
        y = min.getY() - 1; // -1 for initial moveNext.
        z = min.getZ();
        final BlockPos size = max.subtract(min);
        final int count = (size.getX() + 1) * (size.getY() + 1) * (size.getZ() + 1);
        blocksPerTick = MathHelper.ceil(count / (float) scanTicks);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void computeScanResults(final Consumer<ScanResult> callback) {
        final World world = player.getEntityWorld();
        final Set<Block> blacklist = Settings.getBlockBlacklistSet();
        for (int i = 0; i < blocksPerTick; i++) {
            if (!moveNext(world)) {
                return;
            }

            if (center.squareDistanceTo(x + 0.5, y + 0.5, z + 0.5) > sqRadius) {
                continue;
            }

            final BlockPos pos = new BlockPos(x, y, z);
            IBlockState state = world.getBlockState(pos);

            if (blacklist.contains(state.getBlock())) {
                continue;
            }

            state = state.getActualState(world, pos);

            if (blacklist.contains(state.getBlock())) {
                continue;
            }

            final int stateId = Block.getStateId(state);
            if (anyFilterMatches(state) && !tryAddToCluster(pos, stateId)) {
                final ScanResultOre result = new ScanResultOre(stateId, pos, STATE_SCANNED_ALPHA);
                callback.accept(result);
                resultClusters.put(pos, result);
                continue;
            }

            if (!scanCommon && !scanRare && !scanFluids) {
                continue;
            }

            if (center.squareDistanceTo(x + 0.5, y + 0.5, z + 0.5) > sqOreRadius) {
                continue;
            }

            final boolean matches = (scanCommon && oresCommon.get(stateId)) || (scanRare && oresRare.get(stateId)) || (scanFluids && fluids.get(stateId));
            if (matches && !tryAddToCluster(pos, stateId)) {
                final ScanResultOre result = new ScanResultOre(stateId, pos);
                callback.accept(result);
                resultClusters.put(pos, result);
            }
        }
    }

    private boolean anyFilterMatches(final IBlockState state) {
        for (final ScanFilter filter : scanFilters) {
            if (filter.matches(state)) {
                return true;
            }
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean isValid(final ScanResult result) {
        return ((ScanResultOre) result).isRoot();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void render(final Entity entity, final List<ScanResult> results, final float partialTicks) {
        final double posX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
        final double posY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
        final double posZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;

        final Vec3d lookVec = entity.getLook(partialTicks).normalize();
        final Vec3d viewerEyes = entity.getPositionEyes(partialTicks);

        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

        GlStateManager.pushMatrix();
        GlStateManager.translate(-posX, -posY, -posZ);

        final Tessellator tessellator = Tessellator.getInstance();
        final VertexBuffer buffer = tessellator.getBuffer();

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        final float colorNormalizer = 1 / 255f;
        for (final ScanResult result : results) {
            final ScanResultOre resultOre = (ScanResultOre) result;

            if (resultOre.bounds.isVecInside(viewerEyes)) {
                nonCulledResults.add(resultOre);
                continue;
            }

            final Vec3d toResult = resultOre.getPosition().subtract(viewerEyes);
            final float lookDirDot = (float) lookVec.dotProduct(toResult.normalize());
            final float sqLookDirDot = lookDirDot * lookDirDot;
            final float sq2LookDirDot = sqLookDirDot * sqLookDirDot;
            final float focusScale = MathHelper.clamp(sq2LookDirDot * sq2LookDirDot + 0.005f, 0.5f, 1f);

            final int color;
            if (blockColors.containsKey(resultOre.stateId)) {
                color = blockColors.get(resultOre.stateId);
            } else {
                color = DEFAULT_COLOR;
            }

            final float r = ((color >> 16) & 0xFF) * colorNormalizer;
            final float g = ((color >> 8) & 0xFF) * colorNormalizer;
            final float b = (color & 0xFF) * colorNormalizer;
            final float a = Math.max(MIN_ALPHA, Math.max(BASE_ALPHA, resultOre.getAlphaOverride()) * focusScale);

            drawCube(resultOre.bounds.minX, resultOre.bounds.minY, resultOre.bounds.minZ,
                     resultOre.bounds.maxX, resultOre.bounds.maxY, resultOre.bounds.maxZ,
                     r, g, b, a, buffer);
        }

        tessellator.draw();

        if (!nonCulledResults.isEmpty()) {
            GlStateManager.disableCull();

            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

            for (final ScanResultOre resultOre : nonCulledResults) {
                final Vec3d toResult = resultOre.getPosition().subtract(viewerEyes);
                final float lookDirDot = (float) lookVec.dotProduct(toResult.normalize());
                final float sqLookDirDot = lookDirDot * lookDirDot;
                final float sq2LookDirDot = sqLookDirDot * sqLookDirDot;
                final float focusScale = MathHelper.clamp(sq2LookDirDot * sq2LookDirDot + 0.005f, 0.5f, 1f);

                final int color;
                if (blockColors.containsKey(resultOre.stateId)) {
                    color = blockColors.get(resultOre.stateId);
                } else {
                    color = DEFAULT_COLOR;
                }

                final float r = ((color >> 16) & 0xFF) * colorNormalizer;
                final float g = ((color >> 8) & 0xFF) * colorNormalizer;
                final float b = (color & 0xFF) * colorNormalizer;
                final float a = Math.max(MIN_ALPHA, Math.max(BASE_ALPHA, resultOre.getAlphaOverride()) * focusScale);

                drawCube(resultOre.bounds.minX, resultOre.bounds.minY, resultOre.bounds.minZ,
                         resultOre.bounds.maxX, resultOre.bounds.maxY, resultOre.bounds.maxZ,
                         r, g, b, a, buffer);
            }

            tessellator.draw();

            GlStateManager.enableCull();
        }

        nonCulledResults.clear();

        GlStateManager.popMatrix();

        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void reset() {
        super.reset();
        scanCommon = scanRare = scanFluids = false;
        scanFilters.clear();
        sqRadius = sqOreRadius = 0;
        min = max = null;
        blocksPerTick = 0;
        x = y = z = 0;
        resultClusters.clear();
    }

    // --------------------------------------------------------------------- //

    @SideOnly(Side.CLIENT)
    private static float computeRadius(final Collection<ItemStack> modules, final float radius) {
        boolean scanOres = false;
        boolean scanState = false;
        for (final ItemStack module : modules) {
            scanOres |= Items.isModuleOreCommon(module);
            scanOres |= Items.isModuleOreRare(module);
            scanState |= Items.isModuleBlock(module);
        }

        if (scanOres && scanState) {
            return radius * Math.max(Constants.MODULE_ORE_RADIUS_MULTIPLIER, Constants.MODULE_BLOCK_RADIUS_MULTIPLIER);
        } else if (scanOres) {
            return radius * Constants.MODULE_ORE_RADIUS_MULTIPLIER;
        } else {
            assert scanState;
            return radius * Constants.MODULE_BLOCK_RADIUS_MULTIPLIER;
        }
    }

    @SideOnly(Side.CLIENT)
    private boolean tryAddToCluster(final BlockPos pos, final int stateId) {
        final BlockPos min = pos.add(-1, -1, -1);
        final BlockPos max = pos.add(1, 1, 1);

        ScanResultOre root = null;
        for (int y = min.getY(); y <= max.getY(); y++) {
            for (int x = min.getX(); x <= max.getX(); x++) {
                for (int z = min.getZ(); z <= max.getZ(); z++) {
                    final BlockPos clusterPos = new BlockPos(x, y, z);
                    final ScanResultOre cluster = resultClusters.get(clusterPos);
                    if (cluster == null) {
                        continue;
                    }
                    if (stateId != cluster.stateId) {
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

    @SideOnly(Side.CLIENT)
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

    @SideOnly(Side.CLIENT)
    private void buildOreCache() {
        Scannable.getLog().info("Building block state lookup table...");

        final long start = System.currentTimeMillis();

        final TObjectIntMap<String> oreColorsByOreName = buildColorTable(Settings.oreColors);
        final TObjectIntMap<String> fluidColorsByFluidName = buildColorTable(Settings.fluidColors);

        final Set<String> oreNamesBlacklist = new HashSet<>(Arrays.asList(Settings.getOreBlacklist()));
        final Set<String> oreNamesCommon = new HashSet<>(Arrays.asList(Settings.getCommonOres()));
        final Set<String> oreNamesRare = new HashSet<>(Arrays.asList(Settings.getRareOres()));
        final Set<String> stateDescsCommon = new HashSet<>(Arrays.asList(Settings.getCommonStates()));
        final Set<String> stateDescsRare = new HashSet<>(Arrays.asList(Settings.getRareStates()));
        final Set<String> fluidBlacklist = new HashSet<>(Arrays.asList(Settings.getFluidBlacklist()));

        final Pattern pattern = Pattern.compile("^ore[A-Z].*$");
        for (final Block block : ForgeRegistries.BLOCKS.getValues()) {
            for (final IBlockState state : block.getBlockState().getValidStates()) {
                final int stateId = Block.getStateId(state);
                final ItemStack stack = BlockUtils.getItemStackFromState(state, null);
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

                        if (oreNamesCommon.contains(name)) {
                            isCommon = true;
                        } else if (oreNamesRare.contains(name) || pattern.matcher(name).matches()) {
                            isRare = true;
                        } else {
                            continue;
                        }

                        if (oreColorsByOreName.containsKey(name)) {
                            blockColors.put(stateId, oreColorsByOreName.get(name));
                        }
                    }

                    if (isCommon) {
                        oresCommon.set(stateId);
                    } else if (isRare) {
                        oresRare.set(stateId);
                    }
                }
            }
        }

        registerStates(stateDescsCommon, oresCommon);
        registerStates(stateDescsRare, oresRare);

        for (final Map.Entry<String, Fluid> entry : FluidRegistry.getRegisteredFluids().entrySet()) {
            final String fluidName = entry.getKey();
            if (fluidBlacklist.contains(fluidName)) {
                continue;
            }

            final Fluid fluid = entry.getValue();
            final Block block = fluid.getBlock();
            if (block == null) {
                continue;
            }

            final IBlockState state = block.getDefaultState();
            final int stateId = Block.getStateId(state);

            if (fluidColorsByFluidName.containsKey(fluidName)) {
                blockColors.put(stateId, fluidColorsByFluidName.get(fluidName));
            } else {
                blockColors.put(stateId, fluid.getColor());
            }

            fluids.set(stateId);
        }

        Scannable.getLog().info("Built    block state lookup table in {} ms.", System.currentTimeMillis() - start);
    }

    @SideOnly(Side.CLIENT)
    private static TObjectIntMap<String> buildColorTable(final String[] colorConfigs) {
        final TObjectIntMap<String> colors = new TObjectIntHashMap<>();

        final Pattern pattern = Pattern.compile("^(?<name>[^\\s=]+)\\s*=\\s*0x(?<color>[a-fA-F0-9]+)$");
        for (final String colorConfig : colorConfigs) {
            final Matcher matcher = pattern.matcher(colorConfig.trim());
            if (!matcher.matches()) {
                Scannable.getLog().warn("Illegal color entry in settings: '{}'", colorConfig.trim());
                continue;
            }

            final String name = matcher.group("name");
            final int color = Integer.parseInt(matcher.group("color"), 16);

            colors.put(name, color);
        }

        return colors;
    }

    private static void registerStates(final Set<String> stateDescs, final BitSet states) {
        for (final String stateDesc : stateDescs) {
            final IBlockState state = parseStateDesc(stateDesc);
            if (state != null) {
                final int stateId = Block.getStateId(state);
                states.set(stateId);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private static IBlockState parseStateDesc(final String stateDesc) {
        final Matcher matcher = STATE_DESC_PATTERN.matcher(stateDesc);
        if (!matcher.matches()) {
            Scannable.getLog().warn("Failed parsing block state: {}", stateDesc);
            return null;
        }

        final String name = matcher.group("name").trim();
        final Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(name));
        if (block == null || block == Blocks.AIR) {
            return null;
        }

        IBlockState state = block.getDefaultState();

        final String serializedProperties = matcher.group("properties");
        if (serializedProperties != null) {
            final Collection<IProperty<?>> blockProperties = state.getPropertyKeys();
            outer:
            for (final String serializedProperty : serializedProperties.split(",")) {
                final String[] keyValuePair = serializedProperty.split("=");
                assert keyValuePair.length == 2;
                final String serializedKey = keyValuePair[0].trim();
                final String serializedValue = keyValuePair[1].trim();
                for (final IProperty property : blockProperties) {
                    if (Objects.equals(property.getName(), serializedKey)) {
                        final Comparable originalValue = state.getValue(property);
                        do {
                            if (Objects.equals(property.getName(state.getValue(property)), serializedValue)) {
                                continue outer;
                            }
                            state = state.cycleProperty(property);
                        }
                        while (!Objects.equals(state.getValue(property), originalValue));
                        Scannable.getLog().warn("Cannot parse property value '{}' for property '{}' of block {}.", serializedValue, serializedKey, name);
                        continue outer;
                    }
                }
                Scannable.getLog().warn("Block {} has no property '{}'.", name, serializedKey);
            }
        }

        return state;
    }

    // --------------------------------------------------------------------- //

    private static final class ScanFilter {
        private final IBlockState reference;
        private final List<IProperty> properties = new ArrayList<>();

        private ScanFilter(final IBlockState state) {
            this.reference = state;
            // TODO Filter for configurable properties (configurable in the block module).
            for (final IProperty<?> property : state.getPropertyKeys()) {
                if (Objects.equals(property.getName(), "variant") || // Vanilla Minecraft.
                    Objects.equals(property.getName(), "type") || // E.g. ThermalFoundation, TiCon, IC2, Immersive Engineering.
                    Objects.equals(property.getName(), "ore") || // E.g. BigReactors.
                    Objects.equals(property.getName(), "oretype")) { // E.g. DeepResonance.
                    properties.add(property);
                }
            }
        }

        @SuppressWarnings("unchecked")
        boolean matches(final IBlockState state) {
            if (reference.getBlock() != state.getBlock()) {
                return false;
            }

            if (properties.isEmpty()) {
                return true;
            }

            for (final IProperty property : properties) {
                if (!state.getPropertyKeys().contains(property)) {
                    continue;
                }
                if (!Objects.equals(state.getValue(property), reference.getValue(property))) {
                    return false;
                }
            }

            return true;
        }
    }

    private static final class ScanResultOre implements ScanResult {
        private final int stateId;
        private AxisAlignedBB bounds;
        @Nullable
        private ScanResultOre parent;
        private float alphaOverride;

        ScanResultOre(final int stateId, final BlockPos pos, final float alphaOverride) {
            bounds = new AxisAlignedBB(pos);
            this.stateId = stateId;
            this.alphaOverride = alphaOverride;
        }

        ScanResultOre(final int stateId, final BlockPos pos) {
            this(stateId, pos, 0f);
        }

        float getAlphaOverride() {
            return alphaOverride;
        }

        boolean isRoot() {
            return parent == null;
        }

        ScanResultOre getRoot() {
            if (parent != null) {
                return parent.getRoot();
            }
            return this;
        }

        void setRoot(final ScanResultOre root) {
            if (parent != null) {
                parent.setRoot(root);
                return;
            }
            if (root == this) {
                return;
            }

            root.bounds = root.bounds.union(bounds);
            parent = root;
        }

        void add(final BlockPos pos) {
            assert parent == null : "Trying to add to non-root node.";
            bounds = bounds.union(new AxisAlignedBB(pos));
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
    }
}
