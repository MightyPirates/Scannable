package li.cil.scannable.common.network;

import li.cil.scannable.api.API;
import li.cil.scannable.common.network.message.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fmllegacy.network.NetworkDirection;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;

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
        registerMessage(MessageStructureRequest.class, MessageStructureRequest::new, NetworkDirection.PLAY_TO_SERVER);
        registerMessage(MessageStructureResponse.class, MessageStructureResponse::new, NetworkDirection.PLAY_TO_CLIENT);
        registerMessage(MessageRemoveConfiguredModuleItemAt.class, MessageRemoveConfiguredModuleItemAt::new, NetworkDirection.PLAY_TO_SERVER);
        registerMessage(MessageSetConfiguredModuleItemAt.class, MessageSetConfiguredModuleItemAt::new, NetworkDirection.PLAY_TO_SERVER);
    }

    // --------------------------------------------------------------------- //

    private static <T extends AbstractMessage> void registerMessage(final Class<T> type, final Function<FriendlyByteBuf, T> decoder, final NetworkDirection direction) {
        INSTANCE.messageBuilder(type, getNextPacketId(), direction)
                .encoder(AbstractMessage::toBytes)
                .decoder(decoder)
                .consumer(AbstractMessage::handleMessage)
                .add();
    }

    private static int getNextPacketId() {
        return nextPacketId++;
    }
}
