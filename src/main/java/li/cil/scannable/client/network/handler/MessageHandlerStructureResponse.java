package li.cil.scannable.client.network.handler;

import li.cil.scannable.client.scanning.ScanResultProviderStructure;
import li.cil.scannable.common.network.message.MessageStructureResponse;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;

public final class MessageHandlerStructureResponse implements IMessageHandler<MessageStructureResponse, IMessage> {
    @Nullable
    @Override
    public IMessage onMessage(final MessageStructureResponse message, final MessageContext context) {
        ScanResultProviderStructure.INSTANCE.setStructures(message.getStructures());

        return null;
    }
}
