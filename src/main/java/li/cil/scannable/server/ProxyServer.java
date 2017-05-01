package li.cil.scannable.server;

import li.cil.scannable.common.ProxyCommon;
import li.cil.scannable.common.Scannable;
import li.cil.scannable.common.gui.GuiHandlerCommon;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public final class ProxyServer extends ProxyCommon {
    @Override
    public void onInit(final FMLInitializationEvent event) {
        super.onInit(event);

        NetworkRegistry.INSTANCE.registerGuiHandler(Scannable.instance, new GuiHandlerCommon());
    }
}
