package li.cil.scannable.util;

import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;

public final class LazyOptionalUtil {
    @Nullable
    public static <T> T orNull(final LazyOptional<T> value) {
        if (value.isPresent()) {
            return value.orElseThrow(RuntimeException::new);
        } else {
            return null;
        }
    }

    private LazyOptionalUtil() {

    }
}
