package li.cil.scannable.common.forge.capabilities;

import li.cil.scannable.api.API;
import li.cil.scannable.common.config.CommonConfig;
import li.cil.scannable.common.energy.forge.ScannerEnergyStorage;
import li.cil.scannable.common.inventory.ScannerItemHandler;
import li.cil.scannable.common.item.ScannerItem;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = API.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ScannerWrapper implements ICapabilityProvider {
    private static final ResourceLocation KEY = new ResourceLocation(API.MOD_ID, "scanner");

    // --------------------------------------------------------------------- //

    private final IItemHandler itemHandler;
    private final IEnergyStorage energyStorage;

    private final LazyOptional<IItemHandler> itemHandlerHolder;
    private final LazyOptional<IEnergyStorage> energyStorageHolder;

    // --------------------------------------------------------------------- //

    public ScannerWrapper(final ItemStack container) {
        itemHandler = new InvWrapper(ScannerItemHandler.of(container));
        energyStorage = ScannerEnergyStorage.of(container);

        itemHandlerHolder = LazyOptional.of(() -> itemHandler);
        energyStorageHolder = LazyOptional.of(() -> energyStorage);
    }

    @SubscribeEvent
    public static void attachScannerModuleCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
        if (event.getObject().getItem() instanceof ScannerItem) {
            event.addCapability(KEY, new ScannerWrapper(event.getObject()));
        }
    }

    // --------------------------------------------------------------------- //
    // ICapabilityProvider

    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull final Capability<T> capability, final @Nullable Direction side) {
        final LazyOptional<T> itemHandlerCapability = ForgeCapabilities.ITEM_HANDLER.orEmpty(capability, itemHandlerHolder);
        if (itemHandlerCapability.isPresent()) {
            return itemHandlerCapability;
        }

        final LazyOptional<T> energyCapability = ForgeCapabilities.ENERGY.orEmpty(capability, energyStorageHolder);
        if (CommonConfig.useEnergy && energyCapability.isPresent()) {
            return energyCapability;
        }
        return LazyOptional.empty();
    }
}
