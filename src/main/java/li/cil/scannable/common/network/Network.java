package li.cil.scannable.common.network;

import li.cil.scannable.api.API;
import li.cil.scannable.common.network.message.AbstractMessage;
import li.cil.scannable.common.network.message.RemoveConfiguredModuleItemAtMessage;
import li.cil.scannable.common.network.message.SetConfiguredModuleItemAtMessage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.function.Function;

public final class Network {
    private static final String PROTOCOL_VERSION = "1";
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
        ServerPlayNetworking.registerGlobalReceiver(location, (server, player, handler, buf, responseSender) -> {
            T message = decoder.apply(buf);
            AbstractMessage.handleMessage(message, server, player, handler, buf, responseSender);
        });
    }

    @Environment(EnvType.CLIENT)
    public static void sendToServer(AbstractMessage message) {
        ResourceLocation loc = PACKET_MAP.get(message.getClass());
        if(loc == null)
            throw new IllegalArgumentException("Invalid message type");
        FriendlyByteBuf buf = PacketByteBufs.create();
        message.toBytes(buf);
        ClientPlayNetworking.send(loc, buf);
    }
}
