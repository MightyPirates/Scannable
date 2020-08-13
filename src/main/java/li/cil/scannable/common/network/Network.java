package li.cil.scannable.common.network;

import li.cil.scannable.api.API;
import li.cil.scannable.common.network.message.MessageStructureRequest;
import li.cil.scannable.common.network.message.MessageStructureResponse;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public final class Network {
    private static final String PROTOCOL_VERSION = "1";
    private static int nextPacketId = 1;

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(API.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int getNextPacketId() {
        return nextPacketId++;
    }

    // --------------------------------------------------------------------- //

    public static void register() {
        INSTANCE.messageBuilder(MessageStructureRequest.class, getNextPacketId())
                .encoder(MessageStructureRequest::toBytes)
                .decoder(MessageStructureRequest::new)
                .consumer(MessageStructureRequest::handle)
                .add();

        INSTANCE.messageBuilder(MessageStructureResponse.class, getNextPacketId())
                .encoder(MessageStructureResponse::toBytes)
                .decoder(MessageStructureResponse::new)
                .consumer(MessageStructureResponse::handle)
                .add();
    }
}
