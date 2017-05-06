package li.cil.scannable.common.network.handler;

import li.cil.scannable.client.scanning.ScanResultProviderStructure;
import li.cil.scannable.common.config.Settings;
import li.cil.scannable.common.network.Network;
import li.cil.scannable.common.network.message.MessageStructureRequest;
import li.cil.scannable.common.network.message.MessageStructureResponse;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public final class MessageHandlerStructureRequest implements IMessageHandler<MessageStructureRequest, IMessage> {
    @Nullable
    @Override
    public IMessage onMessage(final MessageStructureRequest message, final MessageContext context) {
        final IThreadListener thread = FMLCommonHandler.instance().getWorldThread(context.netHandler);
        if (thread.isCallingFromMinecraftThread()) {
            onMessageSynchronized(message, context);
        } else {
            thread.addScheduledTask(() -> onMessageSynchronized(message, context));
        }

        return null;
    }

    private void onMessageSynchronized(final MessageStructureRequest message, final MessageContext context) {
        final int dimension = message.getDimension();
        final WorldServer world = DimensionManager.getWorld(dimension);
        if (world == null) {
            return;
        }

        final BlockPos center = message.getCenter();
        final float radius = message.getRadius();
        final boolean hideExplored = message.hideExplored();

        final List<ScanResultProviderStructure.StructureLocation> structures = new ArrayList<>();
        final float sqRadius = radius * radius;
        for (final String name : Settings.getStructures()) {
            final BlockPos pos = world.getChunkProvider().getStrongholdGen(world, name, center, hideExplored);
            if (pos != null && center.distanceSq(pos) <= sqRadius) {
                structures.add(new ScanResultProviderStructure.StructureLocation(name, pos));
            }
        }

        if (structures.isEmpty()) {
            return;
        }

        Network.INSTANCE.getWrapper().sendTo(new MessageStructureResponse(structures.toArray(new ScanResultProviderStructure.StructureLocation[structures.size()])), context.getServerHandler().player);
    }
}
