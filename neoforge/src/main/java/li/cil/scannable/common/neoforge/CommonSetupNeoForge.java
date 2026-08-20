package li.cil.scannable.common.neoforge;

import li.cil.scannable.api.API;
import li.cil.scannable.common.CommonSetup;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(API.MOD_ID)
public final class CommonSetupNeoForge {
    public static ModContainer MOD_CONTAINER;

    public CommonSetupNeoForge(final ModContainer modContainer) {
        MOD_CONTAINER = modContainer;
        CommonSetup.initialize();
    }
}
