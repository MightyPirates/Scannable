package li.cil.scannable.common.network;

import li.cil.scannable.api.API;
import li.cil.scannable.common.network.message.AbstractMessage;
import li.cil.scannable.common.network.message.RemoveConfiguredModuleItemAtMessage;
import li.cil.scannable.common.network.message.SetConfiguredModuleItemAtMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Function;

public final class Network {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(API.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    // --------------------------------------------------------------------- //

    private static int nextPacketId = 1;

    // --------------------------------------------------------------------- //

    public static void initialize() {
        registerMessage(RemoveConfiguredModuleItemAtMessage.class, RemoveConfiguredModuleItemAtMessage::new, NetworkDirection.PLAY_TO_SERVER);
        registerMessage(SetConfiguredModuleItemAtMessage.class, SetConfiguredModuleItemAtMessage::new, NetworkDirection.PLAY_TO_SERVER);
    }

    // --------------------------------------------------------------------- //

    private static <T extends AbstractMessage> void registerMessage(final Class<T> type, final Function<FriendlyByteBuf, T> decoder, final NetworkDirection direction) {
        INSTANCE.messageBuilder(type, getNextPacketId(), direction)
                .encoder(AbstractMessage::toBytes)
                .decoder(decoder)
                .consumerMainThread((message, context) -> message.handleMessage(context.get()))
                .add();
    }

    private static int getNextPacketId() {
        return nextPacketId++;
    }
}
