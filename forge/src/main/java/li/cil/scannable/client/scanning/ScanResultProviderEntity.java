package li.cil.scannable.client.scanning;

import com.mojang.blaze3d.vertex.PoseStack;
import li.cil.scannable.api.API;
import li.cil.scannable.api.prefab.AbstractScanResultProvider;
import li.cil.scannable.api.scanning.EntityScannerModule;
import li.cil.scannable.api.scanning.ScanResult;
import li.cil.scannable.api.scanning.ScanResultRenderContext;
import li.cil.scannable.api.scanning.ScannerModule;
import li.cil.scannable.common.capabilities.Capabilities;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

@OnlyIn(Dist.CLIENT)
public final class ScanResultProviderEntity extends AbstractScanResultProvider {
    private final List<Predicate<Entity>> filters = new ArrayList<>();
    private final Map<Predicate<Entity>, EntityScannerModule> filterToModule = new HashMap<>();
    private final ArrayList<Entity> entities = new ArrayList<>();
    private int currentEntityIndex, entitiesStep;
    private final List<ScanResultEntity> results = new ArrayList<>();

    // --------------------------------------------------------------------- //
    // ScanResultProvider

    @Override
    public void initialize(final Player player, final Collection<ItemStack> modules, final Vec3 center, final float radius, final int scanTicks) {
        super.initialize(player, modules, center, radius, scanTicks);

        filters.clear();
        filterToModule.clear();
        for (final ItemStack stack : modules) {
            final LazyOptional<ScannerModule> capability = stack.getCapability(Capabilities.SCANNER_MODULE_CAPABILITY);
            capability.ifPresent(module -> {
                if (module instanceof EntityScannerModule entityModule) {
                    final Predicate<Entity> filter = entityModule.getFilter(stack);
                    filters.add(filter);
                    filterToModule.put(filter, entityModule);
                }
            });
        }

        entities.clear();
        for (final Entity entity : player.level.getEntities().getAll()) {
            entities.add(entity);
        }
        currentEntityIndex = 0;
        entitiesStep = Mth.ceil(entities.size() / (float) scanTicks);
    }

    @Override
    public void computeScanResults() {
        for (final int end = Math.min(currentEntityIndex + entitiesStep, entities.size()); currentEntityIndex < end; currentEntityIndex++) {
            final Entity entity = entities.get(currentEntityIndex);
            if (!entity.isAlive()) {
                continue;
            }

            final Vec3 position = entity.position();
            if (center.distanceToSqr(position) < radius * radius) {
                ResourceLocation icon = API.ICON_INFO;
                boolean hasMatch = false;
                for (final Predicate<Entity> filter : filters) {
                    if (filter.test(entity)) {
                        hasMatch = true;
                        final Optional<ResourceLocation> filterIcon = filterToModule.get(filter).getIcon(entity);
                        if (filterIcon.isPresent()) {
                            icon = filterIcon.get();
                            break;
                        }
                    }
                }
                if (hasMatch) {
                    results.add(new ScanResultEntity(entity, icon));
                }
            }
        }
    }

    @Override
    public void collectScanResults(final BlockGetter level, final Consumer<ScanResult> callback) {
        results.forEach(callback);
    }

    @Override
    public void render(final ScanResultRenderContext context, final MultiBufferSource bufferSource, final PoseStack poseStack, final Camera renderInfo, final float partialTicks, final List<ScanResult> results) {
        if (context != ScanResultRenderContext.GUI) {
            return;
        }

        final float yaw = renderInfo.getYRot();
        final float pitch = renderInfo.getXRot();

        final Vec3 lookVec = new Vec3(renderInfo.getLookVector());
        final Vec3 viewerEyes = renderInfo.getPosition();

        final boolean showDistance = renderInfo.getEntity().isShiftKeyDown();

        // Order results by distance to center of screen (deviation from look
        // vector) so that labels we're looking at are in front of others.
        results.sort(Comparator.comparing(result -> {
            final ScanResultEntity resultEntity = (ScanResultEntity) result;
            final Vec3 entityEyes = resultEntity.entity.getEyePosition(partialTicks);
            final Vec3 toResult = entityEyes.subtract(viewerEyes);
            return lookVec.dot(toResult.normalize());
        }));

        for (final ScanResult result : results) {
            final ScanResultEntity resultEntity = (ScanResultEntity) result;
            final Component name = resultEntity.entity.getName();
            final ResourceLocation icon = resultEntity.getIcon();
            final Vec3 resultPos = resultEntity.entity.getEyePosition(partialTicks);
            final float distance = showDistance ? (float) resultPos.subtract(viewerEyes).length() : 0f;
            renderIconLabel(bufferSource, poseStack, yaw, pitch, lookVec, viewerEyes, distance, resultPos, icon, name);
        }
    }

    @Override
    public void reset() {
        super.reset();
        filters.clear();
        filterToModule.clear();
        currentEntityIndex = 0;
        entitiesStep = 0;
        entities.clear();
        results.clear();
    }

    // --------------------------------------------------------------------- //

    private record ScanResultEntity(Entity entity, ResourceLocation icon) implements ScanResult {
        public ResourceLocation getIcon() {
            return icon;
        }

        // --------------------------------------------------------------------- //
        // ScanResult

        @Override
        public Vec3 getPosition() {
            return entity.position();
        }

        @Override
        public AABB getRenderBounds() {
            return entity.getBoundingBoxForCulling();
        }
    }
}
