package li.cil.scannable.common.capabilities;

import li.cil.scannable.api.scanning.ScannerModule;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public final class Capabilities {
    @CapabilityInject(ScannerModule.class)
    public static Capability<ScannerModule> SCANNER_MODULE_CAPABILITY = null;

    // --------------------------------------------------------------------- //

    public static void initialize() {
        CapabilityManager.INSTANCE.register(ScannerModule.class);
    }
}
