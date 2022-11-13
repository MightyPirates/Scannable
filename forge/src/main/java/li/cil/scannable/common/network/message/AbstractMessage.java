package li.cil.scannable.common.network.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public abstract class AbstractMessage {
    protected AbstractMessage() {
    }

    protected AbstractMessage(final FriendlyByteBuf buffer) {
        fromBytes(buffer);
    }

    // --------------------------------------------------------------------- //

    public abstract void handleMessage(final NetworkEvent.Context context);

    public abstract void fromBytes(final FriendlyByteBuf buffer);

    public abstract void toBytes(final FriendlyByteBuf buffer);
}
