/* SPDX-License-Identifier: MIT */

package li.cil.scannable.common.scanning.neoforge;

import li.cil.scannable.common.neoforge.ModEventBus;
import li.cil.scannable.common.scanning.ProviderCacheManager;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TagsUpdatedEvent;

public final class ProviderCacheManagerImpl {
    public static void initialize() {
        ModEventBus.INSTANCE.addListener((final ModConfigEvent event) -> ProviderCacheManager.clearCache());
        NeoForge.EVENT_BUS.addListener(ProviderCacheManagerImpl::handleTagsUpdatedEvent);
    }

    private static void handleTagsUpdatedEvent(final TagsUpdatedEvent event) {
        if (event.shouldUpdateStaticData()) {
            ProviderCacheManager.clearCache();
        }
    }
}
