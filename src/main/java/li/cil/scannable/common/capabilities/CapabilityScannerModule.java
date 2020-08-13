package li.cil.scannable.common.capabilities;

import li.cil.scannable.api.scanning.ScanResultProvider;
import li.cil.scannable.api.scanning.ScannerModule;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;

public final class CapabilityScannerModule {
    @CapabilityInject(ScannerModule.class)
    public static Capability<ScannerModule> SCANNER_MODULE_CAPABILITY;

    public static void register() {
        CapabilityManager.INSTANCE.register(ScannerModule.class, StorageScannerModule.INSTANCE, () -> NullScannerModule.INSTANCE);
    }

    private CapabilityScannerModule() {
    }

    private enum NullScannerModule implements ScannerModule {
        INSTANCE;

        @OnlyIn(Dist.CLIENT)
        @Override
        public int getEnergyCost(final PlayerEntity player, final ItemStack module) {
            return 0;
        }

        @Override
        public boolean hasResultProvider() {
            return false;
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        public ScanResultProvider getResultProvider() {
            return null;
        }
    }

    private enum StorageScannerModule implements Capability.IStorage<ScannerModule> {
        INSTANCE;

        @Nullable
        @Override
        public INBT writeNBT(final Capability<ScannerModule> capability, final ScannerModule instance, final Direction side) {
            return null;
        }

        @Override
        public void readNBT(final Capability<ScannerModule> capability, final ScannerModule instance, final Direction side, final INBT nbt) {
        }
    }
}
