package li.cil.scannable.client.scanning;

import com.mojang.blaze3d.matrix.MatrixStack;
import li.cil.scannable.api.API;
import li.cil.scannable.api.prefab.AbstractScanResultProvider;
import li.cil.scannable.api.scanning.ScanFilterEntity;
import li.cil.scannable.api.scanning.ScanResult;
import li.cil.scannable.api.scanning.ScannerModule;
import li.cil.scannable.api.scanning.ScannerModuleEntity;
import li.cil.scannable.common.capabilities.CapabilityScannerModule;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;

import java.util.*;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public final class ScanResultProviderEntity extends AbstractScanResultProvider {
    public static final ScanResultProviderEntity INSTANCE = new ScanResultProviderEntity();

    // --------------------------------------------------------------------- //

    private final List<ScanFilterEntity> scanFilters = new ArrayList<>();
    private AxisAlignedBB bounds;
    private int minX, maxX, minZ, maxZ;
    private int chunksPerTick;
    private int x, z;
    private final List<LivingEntity> entities = new ArrayList<>();
    private final List<ScanResultEntity> results = new ArrayList<>();

    // --------------------------------------------------------------------- //
    // ScanResultProvider

    @Override
    public void initialize(final PlayerEntity player, final Collection<ItemStack> modules, final Vector3d center, final float radius, final int scanTicks) {
        super.initialize(player, modules, center, radius, scanTicks);

        scanFilters.clear();
        for (final ItemStack module : modules) {
            final LazyOptional<ScannerModule> capability = module.getCapability(CapabilityScannerModule.SCANNER_MODULE_CAPABILITY);
            capability
                    .filter(c -> c instanceof ScannerModuleEntity)
                    .ifPresent(c -> {
                        final Optional<ScanFilterEntity> filter = ((ScannerModuleEntity) c).getFilter(module);
                        filter.ifPresent(scanFilters::add);
                    });
        }

        bounds = new AxisAlignedBB(center.x - radius, center.y - radius, center.z - radius,
                center.x + radius, center.y + radius, center.z + radius);

        final double maxEntityRadius = player.level.getMaxEntityRadius();
        minX = MathHelper.floor((bounds.minX - maxEntityRadius) / 16f);
        maxX = MathHelper.ceil((bounds.maxX + maxEntityRadius) / 16f);
        minZ = MathHelper.floor((bounds.minZ - maxEntityRadius) / 16f);
        maxZ = MathHelper.ceil((bounds.maxZ + maxEntityRadius) / 16f);
        x = minX - 1; // -1 for initial moveNext.
        z = minZ;

        final int count = (maxX - minX + 1) * (maxZ - minZ + 1);
        chunksPerTick = MathHelper.ceil(count / (float) scanTicks);
    }

    @Override
    public void computeScanResults() {
        final World world = player.getCommandSenderWorld();
        for (int i = 0; i < chunksPerTick; i++) {
            if (!moveNext()) {
                return;
            }

            world.getChunk(x, z).getEntitiesOfClass(LivingEntity.class, bounds, entities, this::FilterEntities);
            for (final LivingEntity entity : entities) {
                if (!entity.isAlive()) {
                    continue;
                }

                final Vector3d position = entity.position();
                if (center.distanceTo(position) < radius) {
                    ResourceLocation icon = API.ICON_INFO;
                    for (final ScanFilterEntity filter : scanFilters) {
                        if (filter.matches(entity)) {
                            final Optional<ResourceLocation> filterIcon = filter.getIcon(entity);
                            if (filterIcon.isPresent()) {
                                icon = filterIcon.get();
                                break;
                            }
                        }
                    }
                    results.add(new ScanResultEntity(entity, icon));
                }
            }
            entities.clear();
        }
    }

    @Override
    public void collectScanResults(final IBlockReader world, final Consumer<ScanResult> callback) {
        results.forEach(callback);
    }

    @Override
    public void render(final IRenderTypeBuffer renderTypeBuffer, final MatrixStack matrixStack, final Matrix4f projectionMatrix, final ActiveRenderInfo renderInfo, final float partialTicks, final List<ScanResult> results) {
        final float yaw = renderInfo.getYRot();
        final float pitch = renderInfo.getXRot();

        final Vector3d lookVec = new Vector3d(renderInfo.getLookVector());
        final Vector3d viewerEyes = renderInfo.getPosition();

        final boolean showDistance = renderInfo.getEntity().isShiftKeyDown();

        // Order results by distance to center of screen (deviation from look
        // vector) so that labels we're looking at are in front of others.
        results.sort(Comparator.comparing(result -> {
            final ScanResultEntity resultEntity = (ScanResultEntity) result;
            final Vector3d entityEyes = resultEntity.entity.getEyePosition(partialTicks);
            final Vector3d toResult = entityEyes.subtract(viewerEyes);
            return lookVec.dot(toResult.normalize());
        }));

        for (final ScanResult result : results) {
            final ScanResultEntity resultEntity = (ScanResultEntity) result;
            final ITextComponent name = resultEntity.entity.getName();
            final ResourceLocation icon = resultEntity.getIcon();
            final Vector3d resultPos = resultEntity.entity.getEyePosition(partialTicks);
            final float distance = showDistance ? (float) resultPos.subtract(viewerEyes).length() : 0f;
            renderIconLabel(renderTypeBuffer, matrixStack, yaw, pitch, lookVec, viewerEyes, distance, resultPos, icon, name);
        }
    }

    @Override
    public void reset() {
        super.reset();
        scanFilters.clear();
        bounds = null;
        minX = maxX = minZ = maxZ = 0;
        chunksPerTick = 0;
        x = z = 0;
        entities.clear();
        results.clear();
    }

    // --------------------------------------------------------------------- //

    @OnlyIn(Dist.CLIENT)
    private boolean moveNext() {
        x++;
        if (x > maxX) {
            x = minX;
            z++;
            if (z > maxZ) {
                chunksPerTick = 0;
                return false;
            }
        }
        return true;
    }

    private <T extends Entity> boolean FilterEntities(final T entity) {
        return scanFilters.stream().anyMatch(f -> f.matches(entity));
    }

    // --------------------------------------------------------------------- //

    private static final class ScanResultEntity implements ScanResult {
        private final Entity entity;
        private final ResourceLocation icon;

        ScanResultEntity(final Entity entity, final ResourceLocation icon) {
            this.entity = entity;
            this.icon = icon;
        }

        public ResourceLocation getIcon() {
            return icon;
        }

        // --------------------------------------------------------------------- //
        // ScanResult

        @Override
        public Vector3d getPosition() {
            return entity.position();
        }

        @Override
        public AxisAlignedBB getRenderBounds() {
            return entity.getBoundingBoxForCulling();
        }
    }

    // --------------------------------------------------------------------- //

    private ScanResultProviderEntity() {
    }

}
