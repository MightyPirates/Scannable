package li.cil.scannable.common.network.message;

import li.cil.scannable.common.container.AbstractModuleContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public final class MessageRemoveConfiguredModuleItemAt extends AbstractMessage {
    private int windowId;
    private int index;

    // --------------------------------------------------------------------- //

    public MessageRemoveConfiguredModuleItemAt(final int windowId, final int index) {
        this.windowId = windowId;
        this.index = index;
    }

    public MessageRemoveConfiguredModuleItemAt(final FriendlyByteBuf buffer) {
        super(buffer);
    }

    // --------------------------------------------------------------------- //

    @Override
    protected void handleMessage(final NetworkEvent.Context context) {
        final ServerPlayer player = context.getSender();
        if (player != null && player.containerMenu != null && player.containerMenu.containerId == windowId) {
            if (player.containerMenu instanceof AbstractModuleContainer) {
                ((AbstractModuleContainer) player.containerMenu).removeItemAt(index);
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
