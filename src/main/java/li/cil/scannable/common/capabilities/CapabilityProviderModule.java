package li.cil.scannable.common.capabilities;

import li.cil.scannable.api.scanning.ScannerModule;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class CapabilityProviderModule implements ICapabilityProvider {
    private final ScannerModule module;

    public CapabilityProviderModule(final ScannerModule module) {
        this.module = module;
    }

    // --------------------------------------------------------------------- //
    // ICapabilityProvider

    @SuppressWarnings("unchecked")
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull final Capability<T> capability, final @Nullable Direction side) {
        if (capability == CapabilityScannerModule.SCANNER_MODULE_CAPABILITY) {
            return (LazyOptional<T>) LazyOptional.of(() -> module);
        }
        return LazyOptional.empty();
    }
}
