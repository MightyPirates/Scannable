package li.cil.scannable.common.item;

import li.cil.scannable.client.scanning.ScanResultProviderOre;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

abstract class AbstractItemScannerModuleOre extends AbstractItemScannerModule {
    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(final ItemStack stack, @Nullable final NBTTagCompound nbt) {
        return ScanResultProviderOre.INSTANCE;
    }
}
