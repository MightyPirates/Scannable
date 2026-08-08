package li.cil.scannable.client;

import li.cil.scannable.client.shader.Shaders;

public class ClientSetup {
    public static void initialize() {
        // Screen factories are registered per loader, because on NeoForge they have
        // to go through RegisterMenuScreensEvent, which has already fired by the time
        // FMLClientSetupEvent (and thus this method) runs.
        Shaders.initialize();
    }
}
