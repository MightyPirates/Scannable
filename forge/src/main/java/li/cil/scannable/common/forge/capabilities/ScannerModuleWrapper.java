package li.cil.scannable.common.forge.capabilities;

import li.cil.scannable.api.API;
import li.cil.scannable.api.scanning.ScannerModule;
import li.cil.scannable.common.item.ScannerModuleItem;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = API.MOD_ID, bus = Bus.FORGE)
public final class ScannerModuleWrapper implements ICapabilityProvider {
    private static final ResourceLocation KEY = new ResourceLocation(API.MOD_ID, "scanner_module");

    // --------------------------------------------------------------------- //

    private final LazyOptional<ScannerModule> holder;

    // --------------------------------------------------------------------- //

    public ScannerModuleWrapper(final ScannerModule module) {
        this.holder = LazyOptional.of(() -> module);
    }

    @SubscribeEvent
    public static void attachScannerModuleCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
        if (event.getObject().getItem() instanceof ScannerModuleItem scannerModuleItem) {
            event.addCapability(KEY, new ScannerModuleWrapper(scannerModuleItem.getModule()));
        }
    }

    // --------------------------------------------------------------------- //
    // ICapabilityProvider

    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull final Capability<T> capability, final @Nullable Direction side) {
        return Capabilities.SCANNER_MODULE_CAPABILITY.orEmpty(capability, holder);
    }
}
