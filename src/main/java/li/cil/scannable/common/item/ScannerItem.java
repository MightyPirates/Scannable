package li.cil.scannable.common.item;

import li.cil.scannable.api.scanning.ScannerModule;
import li.cil.scannable.client.ScanManager;
import li.cil.scannable.client.audio.SoundManager;
import li.cil.scannable.common.capabilities.Capabilities;
import li.cil.scannable.common.capabilities.ScannerWrapper;
import li.cil.scannable.common.config.CommonConfig;
import li.cil.scannable.common.config.Strings;
import li.cil.scannable.common.container.ScannerContainerMenu;
import li.cil.scannable.common.energy.ScannerEnergyStorage;
import li.cil.scannable.common.inventory.ScannerItemHandler;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public final class ScannerItem extends ModItem {
    public static boolean isScanner(final ItemStack stack) {
        return stack.getItem() == Items.SCANNER.get();
    }

    // --------------------------------------------------------------------- //

    public ScannerItem() {
        super(new Properties().stacksTo(1));
    }

    // --------------------------------------------------------------------- //
    // Item

    @Override
    public ICapabilityProvider initCapabilities(final ItemStack stack, @Nullable final CompoundTag tag) {
        return new ScannerWrapper(stack);
    }

    @Override
    public void fillItemCategory(final CreativeModeTab group, final NonNullList<ItemStack> items) {
        super.fillItemCategory(group, items);

        if (allowdedIn(group) && CommonConfig.useEnergy) {
            final ItemStack stack = new ItemStack(this);
            ScannerEnergyStorage.of(stack).receiveEnergy(Integer.MAX_VALUE, false);
            items.add(stack);
        }
    }

    @Override
    public void appendHoverText(final ItemStack stack, @Nullable final Level level, final List<Component> tooltip, final TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        if (CommonConfig.useEnergy) {
            final ScannerEnergyStorage energyStorage = ScannerEnergyStorage.of(stack);
            tooltip.add(Strings.energyStorage(energyStorage.getEnergyStored(), energyStorage.getMaxEnergyStored()));
        }
    }

    @Override
    public boolean isBarVisible(final ItemStack stack) {
        return CommonConfig.useEnergy;
    }

    @Override
    public int getBarWidth(final ItemStack stack) {
        return (int) (getRelativeEnergy(stack) * MAX_BAR_WIDTH);
    }

    @Override
    public int getBarColor(final ItemStack stack) {
        return Mth.hsvToRgb(getRelativeEnergy(stack) / 3f, 1, 1);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(final Level level, final Player player, final InteractionHand hand) {
        final ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
                stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(itemHandler -> {
                    if (itemHandler instanceof ScannerItemHandler scannerItemHandler) {
                        NetworkHooks.openGui(serverPlayer, new MenuProvider() {
                            @Override
                            public Component getDisplayName() {
                                return stack.getHoverName();
                            }

                            @Override
                            public AbstractContainerMenu createMenu(final int id, final Inventory inventory, final Player player) {
                                return new ScannerContainerMenu(id, inventory, hand, scannerItemHandler);
                            }
                        }, buffer -> buffer.writeEnum(hand));
                    }
                });
            }
        } else {
            final List<ItemStack> modules = new ArrayList<>();
            if (!collectModules(stack, modules)) {
                if (!level.isClientSide()) {
                    player.displayClientMessage(Strings.MESSAGE_NO_SCAN_MODULES, true);
                }
                player.getCooldowns().addCooldown(this, 10);
                return InteractionResultHolder.fail(stack);
            }

            if (!tryConsumeEnergy(player, stack, modules, true)) {
                if (!level.isClientSide()) {
                    player.displayClientMessage(Strings.MESSAGE_NOT_ENOUGH_ENERGY, true);
                }
                player.getCooldowns().addCooldown(this, 10);
                return InteractionResultHolder.fail(stack);
            }

            player.startUsingItem(hand);
            if (level.isClientSide()) {
                ScanManager.beginScan(player, modules);
                SoundManager.playChargingSound();
            }
        }

        return InteractionResultHolder.success(stack);
    }

    @Override
    public boolean shouldCauseReequipAnimation(final ItemStack oldStack, final ItemStack newStack, final boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem() || slotChanged;
    }

    @Override
    public int getUseDuration(final ItemStack stack) {
        return ScanManager.SCAN_COMPUTE_DURATION;
    }

    @Override
    public void onUsingTick(final ItemStack stack, final LivingEntity entity, final int count) {
        if (entity.level.isClientSide()) {
            ScanManager.updateScan(entity, false);
        }
    }

    @Override
    public void releaseUsing(final ItemStack stack, final Level level, final LivingEntity entity, final int timeLeft) {
        if (level.isClientSide()) {
            ScanManager.cancelScan();
            SoundManager.stopChargingSound();
        }
        super.releaseUsing(stack, level, entity, timeLeft);
    }

    @Override
    public ItemStack finishUsingItem(final ItemStack stack, final Level level, final LivingEntity entity) {
        if (!(entity instanceof final Player player)) {
            return stack;
        }

        if (level.isClientSide()) {
            SoundCanceler.cancelEquipSound();
        }

        final List<ItemStack> modules = new ArrayList<>();
        if (!collectModules(stack, modules)) {
            return stack;
        }

        final boolean hasEnergy = tryConsumeEnergy((Player) entity, stack, modules, false);
        if (level.isClientSide()) {
            SoundManager.stopChargingSound();

            if (hasEnergy) {
                ScanManager.updateScan(entity, true);
                SoundManager.playActivateSound();
            } else {
                ScanManager.cancelScan();
            }
        }

        player.getCooldowns().addCooldown(this, 40);

        return stack;
    }

    // --------------------------------------------------------------------- //

    static int getModuleEnergyCost(final ItemStack stack) {
        if (!CommonConfig.useEnergy || Capabilities.SCANNER_MODULE_CAPABILITY == null) {
            return 0;
        }

        return stack.getCapability(Capabilities.SCANNER_MODULE_CAPABILITY)
                .map(module -> module.getEnergyCost(stack)).orElse(0);
    }

    private static float getRelativeEnergy(final ItemStack stack) {
        if (!CommonConfig.useEnergy) {
            return 0;
        }

        return stack.getCapability(CapabilityEnergy.ENERGY)
                .map(storage -> storage.getEnergyStored() / (float) storage.getMaxEnergyStored())
                .orElse(0f);
    }

    private static boolean tryConsumeEnergy(final Player player, final ItemStack scanner, final List<ItemStack> modules, final boolean simulate) {
        if (!CommonConfig.useEnergy) {
            return true;
        }

        if (player.isCreative()) {
            return true;
        }

        final LazyOptional<IEnergyStorage> energyStorage = scanner.getCapability(CapabilityEnergy.ENERGY);
        if (!energyStorage.isPresent()) {
            return false;
        }

        int totalCostAccumulator = 0;
        for (final ItemStack module : modules) {
            totalCostAccumulator += getModuleEnergyCost(module);
        }
        final int totalCost = totalCostAccumulator;

        final int extracted = energyStorage.map(storage -> storage.extractEnergy(totalCost, simulate)).orElse(0);
        if (extracted < totalCost) {
            return false;
        }

        return true;
    }

    private static boolean collectModules(final ItemStack scanner, final List<ItemStack> modules) {
        return scanner.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map(itemHandler -> {
            boolean hasScannerModules = false;
            if (itemHandler instanceof ScannerItemHandler scannerItemHandler) {
                final IItemHandler activeModules = scannerItemHandler.getActiveModules();
                for (int slot = 0; slot < activeModules.getSlots(); slot++) {
                    final ItemStack module = activeModules.getStackInSlot(slot);
                    if (module.isEmpty()) {
                        continue;
                    }

                    modules.add(module);

                    hasScannerModules |= module.getCapability(Capabilities.SCANNER_MODULE_CAPABILITY)
                            .map(ScannerModule::hasResultProvider).orElse(false);
                }
            }
            return hasScannerModules;
        }).orElse(false);
    }

    // --------------------------------------------------------------------- //

    // Used to suppress the re-equip sound after finishing a scan (due to potential scanner item stack data change).
    private enum SoundCanceler {
        INSTANCE;

        public static void cancelEquipSound() {
            MinecraftForge.EVENT_BUS.register(SoundCanceler.INSTANCE);
        }

        @SubscribeEvent
        public void onPlaySoundAtEntityEvent(final PlaySoundAtEntityEvent event) {
            if (event.getSound() == SoundEvents.ARMOR_EQUIP_GENERIC) {
                event.setCanceled(true);
            }
            MinecraftForge.EVENT_BUS.unregister(this);
        }
    }
}
