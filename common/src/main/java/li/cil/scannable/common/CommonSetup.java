package li.cil.scannable.common;

import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import li.cil.scannable.api.API;
import li.cil.scannable.client.ClientConfig;
import li.cil.scannable.client.scanning.ScanResultProviders;
import li.cil.scannable.common.config.CommonConfig;
import li.cil.scannable.common.container.Containers;
import li.cil.scannable.common.item.Items;
import li.cil.scannable.common.network.Network;
import li.cil.scannable.common.scanning.ProviderCacheManager;
import li.cil.scannable.common.tags.ItemTags;
import li.cil.scannable.util.ConfigManager;
import li.cil.scannable.util.RegistryUtils;

public final class CommonSetup {
    public static void initialize() {
        ConfigManager.add(CommonConfig::new);
        ConfigManager.add(ClientConfig::new);
        ConfigManager.initialize();

        RegistryUtils.begin(API.MOD_ID);

        ItemTags.initialize();
        Items.initialize();
        Containers.initialize();
        Network.initialize();

        EnvExecutor.runInEnv(Env.CLIENT, () -> ScanResultProviders::initialize);
        EnvExecutor.runInEnv(Env.CLIENT, () -> ProviderCacheManager::initialize);

        RegistryUtils.finish();

        ModCreativeTabs.initialize();
    }
}
