package li.cil.scannable.common.network.message;

import dev.architectury.networking.NetworkManager;
import li.cil.scannable.common.container.AbstractModuleContainerMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

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
    public void handleMessage(final NetworkManager.PacketContext context) {
        if (context.getPlayer() instanceof ServerPlayer player &&
            player.containerMenu != null &&
            player.containerMenu.containerId == windowId &&
            player.containerMenu instanceof AbstractModuleContainerMenu) {
            ((AbstractModuleContainerMenu) player.containerMenu).setItemAt(index, value);
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
