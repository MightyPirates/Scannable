package li.cil.scannable.common.capabilities;

import li.cil.scannable.api.scanning.ScannerModule;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ScannerModuleWrapper implements ICapabilityProvider {
    private final LazyOptional<ScannerModule> holder;

    // --------------------------------------------------------------------- //

    public ScannerModuleWrapper(final ScannerModule module) {
        this.holder = LazyOptional.of(() -> module);
    }

    // --------------------------------------------------------------------- //
    // ICapabilityProvider

    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull final Capability<T> capability, final @Nullable Direction side) {
        return Capabilities.SCANNER_MODULE_CAPABILITY.orEmpty(capability, holder);
    }
}
