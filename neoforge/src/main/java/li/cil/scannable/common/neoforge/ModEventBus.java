/* SPDX-License-Identifier: MIT */

package li.cil.scannable.common.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;

public final class ModEventBus {
    public static IEventBus INSTANCE;

    /**
     * Needed to register configs; {@code ModLoadingContext.registerConfig} was
     * removed in NeoForge 21.
     */
    public static ModContainer MOD_CONTAINER;

    private ModEventBus() {
    }
}
