package li.cil.scannable.common.network.message;

import com.google.common.collect.BiMap;
import io.netty.buffer.ByteBuf;
import li.cil.scannable.client.scanning.ScanResultProviderStructure;
import li.cil.scannable.common.config.Settings;
import li.cil.scannable.common.network.Network;
import li.cil.scannable.util.Migration;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.registries.GameData;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class MessageStructureRequest {
    private ResourceLocation dimension;
    private BlockPos center;
    private int radius;
    private boolean skipExistingChunks;

    // --------------------------------------------------------------------- //

    public MessageStructureRequest(final World world, final BlockPos center, final float radius, final boolean skipExistingChunks) {
        this.dimension = Migration.RegistryKey.getResourceLocation(Migration.World.getDimension(world));
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
            final ServerPlayerEntity sender = context.getSender();
            if (sender == null) {
                return;
            }

            final MinecraftServer server = sender.getServer();
            if (server == null) {
                return;
            }

            final ServerWorld world = server.getWorld(Migration.RegistryKey.getKey(Registry.WORLD_KEY, message.dimension));
            if (world == null) {
                return;
            }

            final BlockPos center = message.center;
            final int radius = message.radius;
            final boolean skipExistingChunks = message.skipExistingChunks;

            final BiMap<String, Structure<?>> structureMap = GameData.getStructureMap();
            final List<ScanResultProviderStructure.StructureLocation> structures = new ArrayList<>();
            final float sqRadius = radius * radius;
            for (final String name : Settings.structures) {
                final Structure<?> structure = structureMap.get(name);
                if (structure == null) {
                    continue;
                }

                final BlockPos pos = Migration.World.findNearestStructure(world, structure, center, radius, skipExistingChunks);
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
        dimension = new ResourceLocation(packet.readString(1024));
        center = packet.readBlockPos();
        radius = packet.readVarInt();
        skipExistingChunks = packet.readBoolean();
    }

    public void toBytes(final ByteBuf buf) {
        final PacketBuffer packet = new PacketBuffer(buf);
        packet.writeString(dimension.toString());
        packet.writeBlockPos(center);
        packet.writeVarInt(radius);
        packet.writeBoolean(skipExistingChunks);
    }
}
