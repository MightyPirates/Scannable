package li.cil.scannable.common.network.message;

import io.netty.buffer.ByteBuf;
import li.cil.scannable.client.scanning.ScanResultProviderStructure;
import li.cil.scannable.common.config.Settings;
import li.cil.scannable.common.network.Network;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class MessageStructureRequest {
    private DimensionType dimension;
    private BlockPos center;
    private int radius;
    private boolean skipExistingChunks;

    // --------------------------------------------------------------------- //

    public MessageStructureRequest(final World world, final BlockPos center, final float radius, final boolean skipExistingChunks) {
        this.dimension = world.getDimension().getType();
        this.center = center;
        this.radius = (int) Math.ceil(radius);
        this.skipExistingChunks = skipExistingChunks;
    }

    public MessageStructureRequest(final ByteBuf buffer) {
        fromBytes(buffer);
    }

    // --------------------------------------------------------------------- //

    public static boolean handle(final MessageStructureRequest message, final Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            final DimensionType dimension = message.dimension;
            final ServerPlayerEntity sender = context.getSender();
            if (sender == null) {
                return;
            }

            final MinecraftServer server = sender.getServer();
            if (server == null) {
                return;
            }

            final ServerWorld world = server.getWorld(dimension);
            final BlockPos center = message.center;
            final int radius = message.radius;
            final boolean skipExistingChunks = message.skipExistingChunks;

            final List<ScanResultProviderStructure.StructureLocation> structures = new ArrayList<>();
            final float sqRadius = radius * radius;
            for (final String name : Settings.structures) {
                final BlockPos pos = world.findNearestStructure(name, center, radius, skipExistingChunks);
                if (pos != null && center.distanceSq(pos) <= sqRadius) {
                    final ITextComponent localizedName = new TranslationTextComponent("structure." + name);
                    structures.add(new ScanResultProviderStructure.StructureLocation(localizedName, pos));
                }
            }

            if (structures.isEmpty()) {
                return;
            }

            Network.INSTANCE.reply(new MessageStructureResponse(structures.toArray(new ScanResultProviderStructure.StructureLocation[0])), context);
        });

        return true;
    }

    // --------------------------------------------------------------------- //

    public void fromBytes(final ByteBuf buf) {
        final PacketBuffer packet = new PacketBuffer(buf);
        dimension = DimensionType.getById(packet.readVarInt());
        center = packet.readBlockPos();
        radius = packet.readVarInt();
        skipExistingChunks = packet.readBoolean();
    }

    public void toBytes(final ByteBuf buf) {
        final PacketBuffer packet = new PacketBuffer(buf);
        packet.writeVarInt(dimension.getId());
        packet.writeBlockPos(center);
        packet.writeVarInt(radius);
        packet.writeBoolean(skipExistingChunks);
    }
}
