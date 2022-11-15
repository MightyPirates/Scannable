package li.cil.scannable.common.network.message;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;

public abstract class AbstractMessage {
    protected AbstractMessage() {
    }

    protected AbstractMessage(final FriendlyByteBuf buffer) {
        fromBytes(buffer);
    }

    // --------------------------------------------------------------------- //

    public abstract void handleMessage(final NetworkManager.PacketContext context);

    public abstract void fromBytes(final FriendlyByteBuf buffer);

    public abstract void toBytes(final FriendlyByteBuf buffer);
}
