package li.cil.scannable.common.network.message;

import io.netty.buffer.ByteBuf;
import li.cil.scannable.common.config.ServerSettings;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public final class MessageConfig implements IMessage {
    private ServerSettings settings;

    // --------------------------------------------------------------------- //

    public MessageConfig() {
        settings = new ServerSettings();
    }

    public ServerSettings getSettings() {
        return settings;
    }

    // --------------------------------------------------------------------- //
    // IMessage

    @Override
    public void fromBytes(final ByteBuf buf) {
        settings = new ServerSettings(new PacketBuffer(buf));
    }

    @Override
    public void toBytes(final ByteBuf buf) {
        settings.writeToBuffer(new PacketBuffer(buf));
    }
}
