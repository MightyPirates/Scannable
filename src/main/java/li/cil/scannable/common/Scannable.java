package li.cil.scannable.common;

import li.cil.scannable.client.ClientConfig;
import li.cil.scannable.common.config.CommonConfig;
import li.cil.scannable.common.container.Containers;
import li.cil.scannable.common.item.Items;
import li.cil.scannable.common.network.Network;
import li.cil.scannable.common.tags.ItemTags;
import li.cil.scannable.util.ConfigManager;
import net.fabricmc.api.ModInitializer;

public final class Scannable implements ModInitializer {
    public void onInitialize() {
        ConfigManager.add(CommonConfig::new);
        ConfigManager.add(ClientConfig::new);
        ConfigManager.initialize();

        ItemTags.initialize();
        Items.initialize();
        Containers.initialize();

        Network.initialize();
    }
}
