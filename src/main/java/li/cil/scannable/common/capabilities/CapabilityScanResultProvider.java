package li.cil.scannable.common.capabilities;

import li.cil.scannable.api.scanning.ScanResult;
import li.cil.scannable.api.scanning.ScanResultProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Consumer;

public final class CapabilityScanResultProvider {
    @CapabilityInject(ScanResultProvider.class)
    public static Capability<ScanResultProvider> SCAN_RESULT_PROVIDER_CAPABILITY;

    public static void register() {
        CapabilityManager.INSTANCE.register(ScanResultProvider.class, StorageScanResultProvider.INSTANCE, () -> NullScanResultProvider.INSTANCE);
    }

    private CapabilityScanResultProvider() {
    }

    private enum NullScanResultProvider implements ScanResultProvider {
        INSTANCE;

        @Override
        public void initialize(final EntityPlayer player, Collection<ItemStack> modules, final Vec3d center, final float radius, final int scanTicks) {
        }

        @Override
        public void computeScanResults(final Consumer<ScanResult> callback) {
        }

        @Override
        public void render(final Entity entity, final Iterable<ScanResult> results, final float partialTicks) {
        }

        @Override
        public void reset() {
        }
    }

    private enum StorageScanResultProvider implements Capability.IStorage<ScanResultProvider> {
        INSTANCE;

        @Nullable
        @Override
        public NBTBase writeNBT(final Capability<ScanResultProvider> capability, final ScanResultProvider instance, final EnumFacing side) {
            return null;
        }

        @Override
        public void readNBT(final Capability<ScanResultProvider> capability, final ScanResultProvider instance, final EnumFacing side, final NBTBase nbt) {

        }
    }
}
