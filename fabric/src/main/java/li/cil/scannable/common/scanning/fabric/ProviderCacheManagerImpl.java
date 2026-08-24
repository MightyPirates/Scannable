/* SPDX-License-Identifier: MIT */

package li.cil.scannable.common.scanning.fabric;

import fuzs.forgeconfigapiport.fabric.api.v5.ModConfigEvents;
import li.cil.scannable.api.API;
import li.cil.scannable.common.scanning.ProviderCacheManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;

public final class ProviderCacheManagerImpl {
    public static void initialize() {
        ModConfigEvents.loading(API.MOD_ID).register(cfg -> ProviderCacheManager.clearCache());
        ModConfigEvents.reloading(API.MOD_ID).register(cfg -> ProviderCacheManager.clearCache());
        CommonLifecycleEvents.TAGS_LOADED.register((registries, client) -> ProviderCacheManager.clearCache());
    }
}
