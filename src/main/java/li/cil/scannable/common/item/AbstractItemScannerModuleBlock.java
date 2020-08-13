package li.cil.scannable.common.item;

import li.cil.scannable.api.scanning.ScannerModuleBlock;
import li.cil.scannable.common.capabilities.CapabilityProviderModule;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

abstract class AbstractItemScannerModuleBlock extends AbstractItemScannerModule {
    private final ICapabilityProvider capabilityProvider;

    protected AbstractItemScannerModuleBlock(final ScannerModuleBlock module) {
        capabilityProvider = new CapabilityProviderModule(module);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(final ItemStack stack, @Nullable final CompoundNBT nbt) {
        return capabilityProvider;
    }
}
