package li.cil.scannable.common.network.message;

import li.cil.scannable.common.container.AbstractModuleContainerMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public final class SetConfiguredModuleItemAtMessage extends AbstractMessage {
    private int windowId;
    private int index;
    private ResourceLocation value;

    // --------------------------------------------------------------------- //

    public SetConfiguredModuleItemAtMessage(final int windowId, final int index, final ResourceLocation value) {
        this.windowId = windowId;
        this.index = index;
        this.value = value;
    }

    public SetConfiguredModuleItemAtMessage(final FriendlyByteBuf buffer) {
        super(buffer);
    }

    // --------------------------------------------------------------------- //

    @Override
    public void handleMessage(final NetworkEvent.Context context) {
        final ServerPlayer player = context.getSender();
        if (player != null && player.containerMenu != null && player.containerMenu.containerId == windowId) {
            if (player.containerMenu instanceof AbstractModuleContainerMenu) {
                ((AbstractModuleContainerMenu) player.containerMenu).setItemAt(index, value);
            }
        }
    }

    @Override
    public void fromBytes(final FriendlyByteBuf buffer) {
        windowId = buffer.readByte();
        index = buffer.readByte();
        value = buffer.readResourceLocation();
    }

    @Override
    public void toBytes(final FriendlyByteBuf buffer) {
        buffer.writeByte(windowId);
        buffer.writeByte(index);
        buffer.writeResourceLocation(value);
    }
}
