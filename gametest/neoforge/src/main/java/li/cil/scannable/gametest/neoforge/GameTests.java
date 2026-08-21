package li.cil.scannable.gametest.neoforge;

import li.cil.scannable.gametest.GameTestReporting;
import net.neoforged.fml.common.Mod;

import java.io.File;

import static li.cil.scannable.gametest.TestSupport.MOD_ID;

@Mod(MOD_ID)
public final class GameTests {
    private static final String JUNIT_OUTPUT_DIR_PROPERTY = "scannable.gameTest.junitDir";
    private static final String REPORT_FILE_NAME = "neoforge-game-tests.xml";

    public GameTests() {
        final String directory = System.getProperty(JUNIT_OUTPUT_DIR_PROPERTY);
        GameTestReporting.install(directory == null || directory.isEmpty()
            ? null
            : new File(directory, REPORT_FILE_NAME));
    }
}
