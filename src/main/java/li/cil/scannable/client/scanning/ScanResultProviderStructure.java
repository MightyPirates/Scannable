package li.cil.scannable.client.scanning;

import com.mojang.blaze3d.vertex.PoseStack;
import li.cil.scannable.api.API;
import li.cil.scannable.api.prefab.AbstractScanResultProvider;
import li.cil.scannable.api.scanning.ScanResult;
import li.cil.scannable.common.capabilities.Capabilities;
import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.item.StructureScannerModuleItem;
import li.cil.scannable.common.network.Network;
import li.cil.scannable.common.network.message.StructureRequestMessage;
import li.cil.scannable.common.scanning.StructureScannerModule;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public final class ScanResultProviderStructure extends AbstractScanResultProvider {
    public record StructureLocation(Component name, BlockPos pos) {
    }

    // --------------------------------------------------------------------- //

    private static final int CHUNK_SIZE = 16;
    private static final StructureLocation[] EMPTY = new StructureLocation[0];

    private boolean hideExplored;
    private int requestDelay;
    private State state;
    private StructureLocation[] structures = EMPTY;
    private final List<ScanResultStructure> results = new ArrayList<>();

    // --------------------------------------------------------------------- //

    public void setStructures(final StructureLocation[] structures) {
        if (state != State.WAIT_RESPONSE) {
            return;
        }

        this.structures = structures;

        state = State.WAIT_RESULT;
    }

    // --------------------------------------------------------------------- //
    // ScanResultProvider

    @Override
    public void initialize(final Player player, final Collection<ItemStack> modules, final Vec3 center, final float radius, final int scanTicks) {
        super.initialize(player, modules, center, radius * Constants.STRUCTURE_MODULE_RADIUS_MULTIPLIER, scanTicks);
        hideExplored = false;
        for (final ItemStack stack : modules) {
            stack.getCapability(Capabilities.SCANNER_MODULE_CAPABILITY).ifPresent(module -> {
                if (module instanceof StructureScannerModule) {
                    hideExplored |= StructureScannerModuleItem.shouldHideExplored(stack);
                }
            });
        }
        requestDelay = scanTicks / 4; // delay a little to avoid making spamming the server *too* easy.
        state = State.WAIT_REQUEST;
        structures = EMPTY;
    }

    @Override
    public void computeScanResults() {
        switch (state) {
            case WAIT_REQUEST -> {
                if (requestDelay-- > 0) {
                    return;
                }

                Network.INSTANCE.sendToServer(new StructureRequestMessage(player.level, new BlockPos(center), radius, hideExplored));

                state = State.WAIT_RESPONSE;
            }
            case WAIT_RESULT -> {
                final float renderDistance = Minecraft.getInstance().options.renderDistance * CHUNK_SIZE;
                final float sqRenderDistance = renderDistance * renderDistance;
                for (final StructureLocation structure : structures) {
                    final Vec3 structureCenter = new Vec3(structure.pos.getX(), structure.pos.getY(), structure.pos.getZ());
                    final Vec3 toStructure = structureCenter.subtract(center);
                    if (toStructure.lengthSqr() > sqRenderDistance) {
                        final Vec3 clippedPos = center.add(toStructure.normalize().scale(renderDistance - 4));
                        results.add(new ScanResultStructure(structure, clippedPos));
                    } else {
                        results.add(new ScanResultStructure(structure, structureCenter));
                    }
                }
                structures = EMPTY;

                state = State.COMPLETE;
            }
        }
    }

    @Override
    public void collectScanResults(final BlockGetter level, final Consumer<ScanResult> callback) {
        results.forEach(callback);
    }

    @Override
    public void render(final MultiBufferSource bufferSource, final PoseStack poseStack, final Camera renderInfo, final float partialTicks, final List<ScanResult> results) {
        final float yaw = renderInfo.getYRot();
        final float pitch = renderInfo.getXRot();

        final Vec3 lookVec = new Vec3(renderInfo.getLookVector());
        final Vec3 viewerEyes = renderInfo.getPosition();

        final boolean showDistance = renderInfo.getEntity().isShiftKeyDown();

        final float renderDistance = Minecraft.getInstance().options.renderDistance * CHUNK_SIZE;
        final float sqRenderDistance = renderDistance * renderDistance;

        for (final ScanResult result : results) {
            final ScanResultStructure resultStructure = (ScanResultStructure) result;
            final Vec3 structureCenter = new Vec3(resultStructure.structure.pos.getX() + 0.5,
                    resultStructure.structure.pos.getY() + 0.5,
                    resultStructure.structure.pos.getZ() + 0.5);
            final Vec3 toStructure = structureCenter.subtract(viewerEyes);
            final Vec3 resultPos;
            if (toStructure.lengthSqr() > sqRenderDistance) {
                resultPos = viewerEyes.add(toStructure.normalize().scale(renderDistance / 2));
            } else {
                resultPos = structureCenter;
            }
            final Component name = resultStructure.structure.name;
            final ResourceLocation icon = API.ICON_INFO;
            final float distance = showDistance ? (float) structureCenter.subtract(viewerEyes).length() : 0f;
            renderIconLabel(bufferSource, poseStack, yaw, pitch, lookVec, viewerEyes, distance, resultPos, icon, name);
        }
    }

    @Override
    public void reset() {
        super.reset();
        requestDelay = 0;
        state = State.WAIT_REQUEST;
        structures = EMPTY;
        results.clear();
    }

    // --------------------------------------------------------------------- //

    private enum State {
        WAIT_REQUEST,
        WAIT_RESPONSE,
        WAIT_RESULT,
        COMPLETE
    }

    private static final class ScanResultStructure implements ScanResult {
        private final StructureLocation structure;
        private final Vec3 center;
        private final AABB bounds;

        ScanResultStructure(final StructureLocation structure, final Vec3 renderCenter) {
            this.structure = structure;
            this.center = renderCenter;
            this.bounds = new AABB(new BlockPos(renderCenter)).inflate(8, 8, 8);
        }

        @Override
        public Vec3 getPosition() {
            return center;
        }

        @Nullable
        @Override
        public AABB getRenderBounds() {
            return bounds;
        }
    }
}
