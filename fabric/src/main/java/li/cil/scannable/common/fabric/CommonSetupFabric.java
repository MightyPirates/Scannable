package li.cil.scannable.common.fabric;

import li.cil.scannable.common.CommonSetup;
import net.fabricmc.api.ModInitializer;

public final class CommonSetupFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        CommonSetup.initialize();
    }
}
