package li.cil.scannable.common.network.message;

import io.netty.buffer.ByteBuf;
import li.cil.scannable.common.container.AbstractModuleContainer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public final class MessageSetConfiguredModuleItemAt {
    private int windowId;
    private int index;
    private String value;

    // --------------------------------------------------------------------- //

    public MessageSetConfiguredModuleItemAt(final int windowId, final int index, final String value) {
        this.windowId = windowId;
        this.index = index;
        this.value = value;
    }

    public MessageSetConfiguredModuleItemAt(final ByteBuf buffer) {
        fromBytes(buffer);
    }

    // --------------------------------------------------------------------- //

    public static boolean handle(final MessageSetConfiguredModuleItemAt message, final Supplier<NetworkEvent.Context> context) {
        final ServerPlayerEntity player = context.get().getSender();
        if (player != null && player.containerMenu != null && player.containerMenu.containerId == message.windowId) {
            if (player.containerMenu instanceof AbstractModuleContainer) {
                ((AbstractModuleContainer) player.containerMenu).setItemAt(message.index, message.value);
            }
        }
        return true;
    }

    // --------------------------------------------------------------------- //

    public void fromBytes(final ByteBuf buffer) {
        final PacketBuffer packet = new PacketBuffer(buffer);
        windowId = packet.readByte();
        index = packet.readByte();
        value = packet.readUtf(1024);
    }

    public void toBytes(final ByteBuf buffer) {
        final PacketBuffer packet = new PacketBuffer(buffer);
        packet.writeByte(windowId);
        packet.writeByte(index);
        packet.writeUtf(value);
    }
}
