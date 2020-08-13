package li.cil.scannable.client.scanning;

import com.mojang.blaze3d.matrix.MatrixStack;
import li.cil.scannable.api.API;
import li.cil.scannable.api.prefab.AbstractScanResultProvider;
import li.cil.scannable.api.scanning.ScanResult;
import li.cil.scannable.common.capabilities.CapabilityScannerModule;
import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.item.ItemScannerModuleStructure;
import li.cil.scannable.common.network.Network;
import li.cil.scannable.common.network.message.MessageStructureRequest;
import li.cil.scannable.common.scanning.ScannerModuleStructure;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public final class ScanResultProviderStructure extends AbstractScanResultProvider {
    public static final ScanResultProviderStructure INSTANCE = new ScanResultProviderStructure();

    public static final class StructureLocation {
        public final ITextComponent name;
        public final BlockPos pos;

        public StructureLocation(final ITextComponent name, final BlockPos pos) {
            this.name = name;
            this.pos = pos;
        }
    }

    // --------------------------------------------------------------------- //

    private static final StructureLocation[] EMPTY = new StructureLocation[0];

    private boolean hideExplored;
    private int requestDelay;
    private State state;
    private StructureLocation[] structures = EMPTY;

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
    public void initialize(final PlayerEntity player, final Collection<ItemStack> modules, final Vec3d center, final float radius, final int scanTicks) {
        super.initialize(player, modules, center, radius * Constants.MODULE_STRUCTURE_RADIUS_MULTIPLIER, scanTicks);
        hideExplored = false;
        for (final ItemStack module : modules) {
            module.getCapability(CapabilityScannerModule.SCANNER_MODULE_CAPABILITY)
                    .filter(c -> c instanceof ScannerModuleStructure)
                    .ifPresent(c -> {
                        hideExplored |= ItemScannerModuleStructure.shouldHideExplored(module);
                    });
        }
        requestDelay = scanTicks / 4; // delay a little to avoid making spamming the server *too* easy.
        state = State.WAIT_REQUEST;
        structures = EMPTY;
    }

    @Override
    public void computeScanResults(final Consumer<ScanResult> callback) {
        switch (state) {
            case WAIT_REQUEST: {
                if (requestDelay-- > 0) {
                    return;
                }

                Network.INSTANCE.sendToServer(new MessageStructureRequest(player.getEntityWorld(), new BlockPos(center), radius, hideExplored));

                state = State.WAIT_RESPONSE;

                break;
            }
            case WAIT_RESULT: {
                final float renderDistance = Minecraft.getInstance().gameSettings.renderDistanceChunks * Constants.CHUNK_SIZE;
                final float sqRenderDistance = renderDistance * renderDistance;
                for (final StructureLocation structure : structures) {
                    final Vec3d structureCenter = new Vec3d(structure.pos);
                    final Vec3d toStructure = structureCenter.subtract(center);
                    if (toStructure.lengthSquared() > sqRenderDistance) {
                        final Vec3d clippedPos = center.add(toStructure.normalize().scale(renderDistance - 4));
                        callback.accept(new ScanResultStructure(structure, clippedPos));
                    } else {
                        callback.accept(new ScanResultStructure(structure, structureCenter));
                    }
                }
                structures = EMPTY;

                state = State.COMPLETE;

                break;
            }
        }
    }

    @Override
    public void render(final IRenderTypeBuffer renderTypeBuffer, final MatrixStack matrixStack, final Matrix4f projectionMatrix, final ActiveRenderInfo renderInfo, final float partialTicks, final List<ScanResult> results) {
        final float yaw = renderInfo.getYaw();
        final float pitch = renderInfo.getPitch();

        final Vec3d lookVec = new Vec3d(renderInfo.getViewVector());
        final Vec3d viewerEyes = renderInfo.getProjectedView();

        final boolean showDistance = renderInfo.getRenderViewEntity().isSneaking();

        final float renderDistance = Minecraft.getInstance().gameSettings.renderDistanceChunks * Constants.CHUNK_SIZE;
        final float sqRenderDistance = renderDistance * renderDistance;

        for (final ScanResult result : results) {
            final ScanResultStructure resultStructure = (ScanResultStructure) result;
            final Vec3d structureCenter = new Vec3d(resultStructure.structure.pos.getX() + 0.5,
                    resultStructure.structure.pos.getY() + 0.5,
                    resultStructure.structure.pos.getZ() + 0.5);
            final Vec3d toStructure = structureCenter.subtract(viewerEyes);
            final Vec3d resultPos;
            if (toStructure.lengthSquared() > sqRenderDistance) {
                resultPos = viewerEyes.add(toStructure.normalize().scale(renderDistance / 2));
            } else {
                resultPos = structureCenter;
            }
            final ITextComponent name = resultStructure.structure.name;
            final ResourceLocation icon = API.ICON_INFO;
            final float distance = showDistance ? (float) structureCenter.subtract(viewerEyes).length() : 0f;
            renderIconLabel(renderTypeBuffer, matrixStack, yaw, pitch, lookVec, viewerEyes, distance, resultPos, icon, name);
        }
    }

    @Override
    public void reset() {
        super.reset();
        requestDelay = 0;
        state = State.WAIT_REQUEST;
        structures = EMPTY;
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
        private final Vec3d center;
        private final AxisAlignedBB bounds;

        ScanResultStructure(final StructureLocation structure, final Vec3d renderCenter) {
            this.structure = structure;
            this.center = renderCenter;
            this.bounds = new AxisAlignedBB(new BlockPos(renderCenter)).grow(8, 8, 8);
        }

        @Override
        public Vec3d getPosition() {
            return center;
        }

        @Nullable
        @Override
        public AxisAlignedBB getRenderBounds() {
            return bounds;
        }
    }

    // --------------------------------------------------------------------- //

    private ScanResultProviderStructure() {
    }
}
