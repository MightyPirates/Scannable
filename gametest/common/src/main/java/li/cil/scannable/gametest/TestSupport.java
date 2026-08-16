package li.cil.scannable.gametest;

import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;

public final class TestSupport {
    public static final String MOD_ID = "scannable_gametest";

    public static GameTestAssertException failure(final GameTestHelper helper, final String message) {
        return helper.assertionException(Component.literal(message));
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

    private TestSupport() {
    }
}
