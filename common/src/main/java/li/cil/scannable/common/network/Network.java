package li.cil.scannable.common.network;

import dev.architectury.networking.NetworkManager;
import li.cil.scannable.common.network.message.RemoveConfiguredModuleItemAtMessage;
import li.cil.scannable.common.network.message.SetConfiguredModuleItemAtMessage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public final class Network {
    public static void initialize() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S,
            RemoveConfiguredModuleItemAtMessage.TYPE,
            RemoveConfiguredModuleItemAtMessage.STREAM_CODEC,
            (message, context) -> context.queue(() -> message.handleMessage(context)));
        NetworkManager.registerReceiver(NetworkManager.Side.C2S,
            SetConfiguredModuleItemAtMessage.TYPE,
            SetConfiguredModuleItemAtMessage.STREAM_CODEC,
            (message, context) -> context.queue(() -> message.handleMessage(context)));
    }

    @Environment(EnvType.CLIENT)
    public static void sendToServer(final CustomPacketPayload message) {
        NetworkManager.sendToServer(message);
    }

    private Network() {
    }
}
