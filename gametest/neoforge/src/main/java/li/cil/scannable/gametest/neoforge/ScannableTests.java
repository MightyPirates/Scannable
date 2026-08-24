/* SPDX-License-Identifier: MIT */

package li.cil.scannable.gametest.neoforge;

import li.cil.scannable.gametest.TestSupport;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.testframework.conf.FrameworkConfiguration;
import net.neoforged.testframework.summary.JUnitSummaryDumper;

import java.nio.file.Path;

@Mod(TestSupport.MOD_ID)
public final class ScannableTests {
    private static final String JUNIT_OUTPUT_DIR_PROPERTY = "scannable.gameTest.junitDir";

    public ScannableTests(final IEventBus modEventBus, final ModContainer modContainer) {
        FrameworkConfiguration.builder(Identifier.fromNamespaceAndPath(TestSupport.MOD_ID, "tests"))
            .dumpers(new JUnitSummaryDumper(Path.of(
                System.getProperty(JUNIT_OUTPUT_DIR_PROPERTY, "../../build/test-results/gameTest"))))
            .build()
            .create()
            .init(modEventBus, modContainer);
    }
}
