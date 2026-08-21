/* SPDX-License-Identifier: MIT */

package li.cil.scannable.gametest;

import li.cil.scannable.common.config.CommonConfig;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;

public final class TestSupport {
    public static final String MOD_ID = "scannable_gametest";
    public static final String TEMPLATE = "empty";

    // --------------------------------------------------------------------- //

    // The helper is unused, but keeps the signature identical to later MC versions, where the
    // exception can only be built through it. Keeps the test bodies portable across versions.
    public static GameTestAssertException failure(final GameTestHelper helper, final String message) {
        return new GameTestAssertException(message);
    }

    public static void assertEquals(final GameTestHelper helper, final String what, final long expected, final long actual) {
        if (expected != actual) {
            throw failure(helper, what + ": expected " + expected + ", got " + actual);
        }
    }

    public static void assertTrue(final GameTestHelper helper, final String what, final boolean condition) {
        if (!condition) {
            throw failure(helper, what);
        }
    }

    public static void withEnergy(final boolean enabled, final Runnable body) {
        final boolean wasEnabled = CommonConfig.useEnergy;
        CommonConfig.useEnergy = enabled;
        try {
            body.run();
        } finally {
            CommonConfig.useEnergy = wasEnabled;
        }
    }

    // --------------------------------------------------------------------- //

    private TestSupport() {
    }
}
