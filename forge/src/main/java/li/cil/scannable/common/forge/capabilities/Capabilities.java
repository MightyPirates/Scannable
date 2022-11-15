package li.cil.scannable.common.forge.capabilities;

import li.cil.scannable.api.scanning.ScannerModule;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class Capabilities {
    public static Capability<ScannerModule> SCANNER_MODULE_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() { });

    // --------------------------------------------------------------------- //

    @SubscribeEvent
    public static void initialize(final RegisterCapabilitiesEvent event) {
        event.register(ScannerModule.class);
    }
}
