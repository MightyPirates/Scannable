package li.cil.scannable.common.network;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import li.cil.scannable.api.API;
import li.cil.scannable.common.network.message.AbstractMessage;
import li.cil.scannable.common.network.message.RemoveConfiguredModuleItemAtMessage;
import li.cil.scannable.common.network.message.SetConfiguredModuleItemAtMessage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.function.Function;

public final class Network {
    private static final HashMap<Class<? extends AbstractMessage>, ResourceLocation> PACKET_MAP = new HashMap<>();

    private static final ResourceLocation SET_CONFIGURED_MODULE_ITEM = new ResourceLocation(API.MOD_ID, "set_module_item");
    private static final ResourceLocation REMOVE_CONFIGURED_MODULE_ITEM = new ResourceLocation(API.MOD_ID, "remove_module_item");

    // --------------------------------------------------------------------- //

    public static void initialize() {
        registerMessageToServer(REMOVE_CONFIGURED_MODULE_ITEM, RemoveConfiguredModuleItemAtMessage.class, RemoveConfiguredModuleItemAtMessage::new);
        registerMessageToServer(SET_CONFIGURED_MODULE_ITEM, SetConfiguredModuleItemAtMessage.class, SetConfiguredModuleItemAtMessage::new);
    }

    // --------------------------------------------------------------------- //

    private static <T extends AbstractMessage> void registerMessageToServer(final ResourceLocation location, final Class<T> type, final Function<FriendlyByteBuf, T> decoder) {
        PACKET_MAP.put(type, location);
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, location, (buffer, context) -> {
            final T message = decoder.apply(buffer);
            context.queue(() -> message.handleMessage(context));
        });
    }

    @Environment(EnvType.CLIENT)
    public static void sendToServer(final AbstractMessage message) {
        final ResourceLocation loc = PACKET_MAP.get(message.getClass());
        if (loc == null)
            throw new IllegalArgumentException("Invalid message type");
        final FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        message.toBytes(buf);
        NetworkManager.sendToServer(loc, buf);
    }
}
