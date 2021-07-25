package li.cil.scannable.common.scanning;

import li.cil.scannable.api.scanning.ScanResultProvider;
import li.cil.scannable.api.scanning.ScannerModule;
import li.cil.scannable.client.scanning.ScanResultProviders;
import li.cil.scannable.common.config.Settings;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum StructureScannerModule implements ScannerModule {
    INSTANCE;

    @Override
    public int getEnergyCost(final ItemStack module) {
        return Settings.energyCostModuleStructure;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ScanResultProvider getResultProvider() {
        return ScanResultProviders.STRUCTURES.get();
    }
}
