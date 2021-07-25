package li.cil.scannable.common.network.message;

import com.google.common.collect.BiMap;
import li.cil.scannable.client.scanning.ScanResultProviderStructure;
import li.cil.scannable.common.config.Settings;
import li.cil.scannable.common.network.Network;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import net.minecraftforge.registries.GameData;

import java.util.ArrayList;
import java.util.List;

public final class StructureRequestMessage extends AbstractMessage {
    private ResourceLocation dimension;
    private BlockPos center;
    private int radius;
    private boolean skipExistingChunks;

    // --------------------------------------------------------------------- //

    public StructureRequestMessage(final Level level, final BlockPos center, final float radius, final boolean skipExistingChunks) {
        this.dimension = level.dimension().location();
        this.center = center;
        this.radius = (int) Math.ceil(radius);
        this.skipExistingChunks = skipExistingChunks;
    }

    public StructureRequestMessage(final FriendlyByteBuf buffer) {
        super(buffer);
    }

    // --------------------------------------------------------------------- //

    @Override
    protected void handleMessage(final NetworkEvent.Context context) {
        final ServerPlayer sender = context.getSender();
        if (sender == null) {
            return;
        }

        final MinecraftServer server = sender.getServer();
        if (server == null) {
            return;
        }

        final ServerLevel level = server.getLevel(ResourceKey.create(Registry.DIMENSION_REGISTRY, dimension));
        if (level == null) {
            return;
        }

        final BiMap<String, StructureFeature<?>> structureMap = GameData.getStructureMap();
        final List<ScanResultProviderStructure.StructureLocation> structures = new ArrayList<>();
        final float sqRadius = radius * radius;
        for (final String name : Settings.structures) {
            final StructureFeature<?> structure = structureMap.get(name);
            if (structure == null) {
                continue;
            }

            final BlockPos pos = level.findNearestMapFeature(structure, center, radius, skipExistingChunks);
            if (pos != null && center.distSqr(pos) <= sqRadius) {
                final Component localizedName = new TranslatableComponent("structure." + name);
                structures.add(new ScanResultProviderStructure.StructureLocation(localizedName, pos));
            }
        }

        if (structures.isEmpty()) {
            return;
        }

        Network.INSTANCE.reply(new StructureResponseMessage(structures.toArray(new ScanResultProviderStructure.StructureLocation[0])), context);
    }

    @Override
    public void fromBytes(final FriendlyByteBuf buffer) {
        dimension = new ResourceLocation(buffer.readUtf(1024));
        center = buffer.readBlockPos();
        radius = buffer.readVarInt();
        skipExistingChunks = buffer.readBoolean();
    }

    @Override
    public void toBytes(final FriendlyByteBuf buffer) {
        buffer.writeUtf(dimension.toString());
        buffer.writeBlockPos(center);
        buffer.writeVarInt(radius);
        buffer.writeBoolean(skipExistingChunks);
    }
}
