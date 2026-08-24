/* SPDX-License-Identifier: MIT */

package li.cil.scannable.gametest.fabric;

import li.cil.scannable.gametest.GameTestReporting;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import java.io.File;

public final class GameTestsFabric implements ModInitializer {
    private static final String REPORT_FILE_PROPERTY = "fabric-api.gametest.report-file";

    @Override
    public void onInitialize() {
        // Make sure our reporter wins.
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            final String report = System.getProperty(REPORT_FILE_PROPERTY);
            GameTestReporting.install(report == null || report.isEmpty() ? null : new File(report));
        });
    }
}
