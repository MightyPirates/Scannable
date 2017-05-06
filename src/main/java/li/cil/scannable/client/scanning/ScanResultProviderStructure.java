package li.cil.scannable.client.scanning;

import li.cil.scannable.api.Icons;
import li.cil.scannable.api.prefab.AbstractScanResultProvider;
import li.cil.scannable.api.scanning.ScanResult;
import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.config.Settings;
import li.cil.scannable.common.item.ItemScannerModuleStructure;
import li.cil.scannable.common.network.Network;
import li.cil.scannable.common.network.message.MessageStructureRequest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public final class ScanResultProviderStructure extends AbstractScanResultProvider {
    public static final ScanResultProviderStructure INSTANCE = new ScanResultProviderStructure();

    public static final class StructureLocation {
        public final String name;
        public final BlockPos pos;

        public StructureLocation(final String name, final BlockPos pos) {
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
    public int getEnergyCost(final EntityPlayer player, final ItemStack module) {
        return Settings.getEnergyCostModuleStructure();
    }

    @Override
    public void initialize(final EntityPlayer player, final Collection<ItemStack> modules, final Vec3d center, final float radius, final int scanTicks) {
        super.initialize(player, modules, center, radius * Constants.MODULE_STRUCTURE_RADIUS_MULTIPLIER, scanTicks);
        hideExplored = false;
        for (final ItemStack module : modules) {
            hideExplored |= ItemScannerModuleStructure.hideExplored(module);
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

                Network.INSTANCE.getWrapper().sendToServer(new MessageStructureRequest(player.getEntityWorld(), new BlockPos(center), radius, hideExplored));

                state = State.WAIT_RESPONSE;

                break;
            }
            case WAIT_RESULT: {
                final float renderDistance = Minecraft.getMinecraft().gameSettings.renderDistanceChunks * Constants.CHUNK_SIZE;
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
    public void render(final Entity entity, final List<ScanResult> results, final float partialTicks) {
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();

        final double posX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
        final double posY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
        final double posZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
        final float yaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks;
        final float pitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;

        final Vec3d lookVec = entity.getLook(partialTicks).normalize();
        final Vec3d viewerEyes = entity.getPositionEyes(partialTicks);

        final boolean showDistance = entity.isSneaking();

        final float renderDistance = Minecraft.getMinecraft().gameSettings.renderDistanceChunks * Constants.CHUNK_SIZE;
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
            final String name = resultStructure.structure.name;
            final ResourceLocation icon = Icons.INFO;
            final float distance = showDistance ? (float) structureCenter.subtract(viewerEyes).lengthVector() : 0f;
            renderIconLabel(posX, posY, posZ, yaw, pitch, lookVec, viewerEyes, distance, resultPos, icon, name);
        }

        GlStateManager.disableBlend();
        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
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
            this.bounds = new AxisAlignedBB(new BlockPos(renderCenter)).expand(8, 8, 8);
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
}
