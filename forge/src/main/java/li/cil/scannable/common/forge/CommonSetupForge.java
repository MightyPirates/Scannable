package li.cil.scannable.common.forge;

import dev.architectury.platform.forge.EventBuses;
import li.cil.scannable.api.API;
import li.cil.scannable.common.CommonSetup;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(API.MOD_ID)
public class CommonSetupForge {
    public CommonSetupForge() {
        EventBuses.registerModEventBus(API.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        CommonSetup.initialize();
    }
}
