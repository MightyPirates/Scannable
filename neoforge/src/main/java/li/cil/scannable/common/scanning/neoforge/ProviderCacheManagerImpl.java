/* SPDX-License-Identifier: MIT */

package li.cil.scannable.common.scanning.neoforge;

import li.cil.scannable.api.API;
import li.cil.scannable.common.scanning.ProviderCacheManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TagsUpdatedEvent;

@EventBusSubscriber(modid = API.MOD_ID)
public final class ProviderCacheManagerImpl {
    public static void initialize() {
        NeoForge.EVENT_BUS.addListener(ProviderCacheManagerImpl::handleTagsUpdatedEvent);
    }

    @SubscribeEvent
    public static void onModConfigEvent(final ModConfigEvent configEvent) {
        ProviderCacheManager.clearCache();
    }

    private static void handleTagsUpdatedEvent(final TagsUpdatedEvent event) {
        if (event.shouldUpdateStaticData()) {
            ProviderCacheManager.clearCache();
        }
    }
}
