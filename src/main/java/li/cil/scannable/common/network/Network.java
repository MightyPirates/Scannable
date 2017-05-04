package li.cil.scannable.common.network;

import li.cil.scannable.api.API;
import li.cil.scannable.client.network.handler.MessageHandlerConfig;
import li.cil.scannable.common.config.Settings;
import li.cil.scannable.common.network.message.MessageConfig;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public enum Network {
    INSTANCE;

    private SimpleNetworkWrapper wrapper;

    private enum Messages {
        Config,
    }

    // --------------------------------------------------------------------- //

    public void init() {
        wrapper = NetworkRegistry.INSTANCE.newSimpleChannel(API.MOD_ID);

        wrapper.registerMessage(MessageHandlerConfig.class, MessageConfig.class, Messages.Config.ordinal(), Side.CLIENT);

        MinecraftForge.EVENT_BUS.register(this);
    }

    public SimpleNetworkWrapper getWrapper() {
        return wrapper;
    }

    // --------------------------------------------------------------------- //

    @SubscribeEvent
    public void onPlayerLoggedIn(final PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            getWrapper().sendTo(new MessageConfig(), (EntityPlayerMP) event.player);
        }
    }

    @SubscribeEvent
    public void onClientDisconnectionFromServer(final FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        Settings.setServerSettings(null);
    }
}
