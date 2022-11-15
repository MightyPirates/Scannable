package li.cil.scannable.common.network.message;

import dev.architectury.networking.NetworkManager;
import li.cil.scannable.common.container.AbstractModuleContainerMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

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
    public void handleMessage(final NetworkManager.PacketContext context) {
        if (context.getPlayer() instanceof ServerPlayer player &&
            player.containerMenu != null &&
            player.containerMenu.containerId == windowId &&
            player.containerMenu instanceof AbstractModuleContainerMenu) {
            ((AbstractModuleContainerMenu) player.containerMenu).removeItemAt(index);
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
