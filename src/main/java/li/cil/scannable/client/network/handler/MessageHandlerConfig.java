package li.cil.scannable.client.network.handler;

import li.cil.scannable.common.config.Settings;
import li.cil.scannable.common.network.message.MessageConfig;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;

public final class MessageHandlerConfig implements IMessageHandler<MessageConfig, IMessage> {
    @Nullable
    @Override
    public IMessage onMessage(final MessageConfig message, final MessageContext context) {
        Settings.setServerSettings(message.getSettings());

        return null;
    }
}
