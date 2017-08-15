package li.cil.scannable.integration.industrialcraft2;

import li.cil.scannable.integration.ModIDs;
import li.cil.scannable.integration.ModProxy;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public enum ModProxyIndustrialCraft2 implements ModProxy {
    INSTANCE;

    @Override
    public boolean isAvailable() {
        return Loader.isModLoaded(ModIDs.IC2);
    }

    @Override
    public void preInit(final FMLPreInitializationEvent event) {
        ElectricItemManagerScanner.init();
    }
}
