package li.cil.scannable.common.network.message;

import li.cil.scannable.common.container.AbstractModuleContainerMenu;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public final class RemoveConfiguredModuleItemAtMessage extends AbstractMessage {
    private int windowId;
    private int index;

    // --------------------------------------------------------------------- //

    public RemoveConfiguredModuleItemAtMessage(final int windowId, final int index) {
        this.windowId = windowId;
        this.index = index;
    }

    public RemoveConfiguredModuleItemAtMessage(final FriendlyByteBuf buffer) {
        super(buffer);
    }

    // --------------------------------------------------------------------- //


    @Override
    protected void handleMessage(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        if (player != null && player.containerMenu != null && player.containerMenu.containerId == windowId) {
            if (player.containerMenu instanceof AbstractModuleContainerMenu) {
                ((AbstractModuleContainerMenu) player.containerMenu).removeItemAt(index);
            }
        }
    }

    @Override
    public void fromBytes(final FriendlyByteBuf buffer) {
        windowId = buffer.readByte();
        index = buffer.readByte();
    }

    @Override
    public void toBytes(final FriendlyByteBuf buffer) {
        buffer.writeByte(windowId);
        buffer.writeByte(index);
    }
}
