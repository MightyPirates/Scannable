package li.cil.scannable.common.item;

import li.cil.scannable.common.capabilities.CapabilityProviderModuleEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

abstract class AbstractItemScannerModuleEntity extends AbstractItemScannerModule {
    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(final ItemStack stack, @Nullable final NBTTagCompound nbt) {
        return CapabilityProviderModuleEntity.INSTANCE;
    }
}
