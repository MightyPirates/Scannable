package li.cil.scannable.common.item;

import li.cil.scannable.api.scanning.ScannerModule;
import li.cil.scannable.client.ScanManager;
import li.cil.scannable.client.audio.SoundManager;
import li.cil.scannable.common.capabilities.Capabilities;
import li.cil.scannable.common.capabilities.ScannerWrapper;
import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.config.Settings;
import li.cil.scannable.common.container.ScannerContainerMenu;
import li.cil.scannable.common.inventory.ItemHandlerScanner;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
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
import net.minecraftforge.fmllegacy.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public final class ScannerItem extends AbstractItem {
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

        if (!allowdedIn(group)) {
            return;
        }

        if (group == CreativeModeTab.TAB_SEARCH) {
            return; // Called before capabilities have been initialized...
        }

        final ItemStack stack = new ItemStack(this);
        final LazyOptional<IEnergyStorage> energyStorage = stack.getCapability(CapabilityEnergy.ENERGY);
        if (!energyStorage.isPresent()) {
            return;
        }

        energyStorage.ifPresent(storage -> storage.receiveEnergy(storage.getMaxEnergyStored(), false));
        items.add(stack);
    }

    @Override
    public void appendHoverText(final ItemStack stack, @Nullable final Level level, final List<Component> tooltip, final TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(new TranslatableComponent(Constants.TOOLTIP_SCANNER));

        if (level == null) {
            return; // Presumably from initial search tree population where capabilities have not yet been initialized.
        }

        if (!Settings.useEnergy) {
            return;
        }

        final LazyOptional<IEnergyStorage> energyStorage = stack.getCapability(CapabilityEnergy.ENERGY);
        if (!energyStorage.isPresent()) {
            return;
        }

        energyStorage.ifPresent(storage -> tooltip.add(
                new TranslatableComponent(Constants.TOOLTIP_SCANNER_ENERGY, storage.getEnergyStored(), storage.getMaxEnergyStored())));
    }

    @Override
    public boolean showDurabilityBar(final ItemStack stack) {
        return Settings.useEnergy;
    }

    @Override
    public double getDurabilityForDisplay(final ItemStack stack) {
        if (!Settings.useEnergy) {
            return 0;
        }

        final LazyOptional<IEnergyStorage> energyStorage = stack.getCapability(CapabilityEnergy.ENERGY);
        if (energyStorage.isPresent()) { // NB: map() has a breaking API change 1.16.3.
            final IEnergyStorage storage = energyStorage.orElseThrow(AssertionError::new);
            return 1 - storage.getEnergyStored() / (float) storage.getMaxEnergyStored();
        } else {
            return 1.0f;
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(final Level level, final Player player, final InteractionHand hand) {
        final ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
                stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(itemHandler -> {
                    if (itemHandler instanceof ItemHandlerScanner scannerItemHandler) {
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
                if (level.isClientSide()) {
                    Minecraft.getInstance().gui.getChat().addMessage(new TranslatableComponent(Constants.MESSAGE_NO_SCAN_MODULES), Constants.CHAT_LINE_ID);
                }
                player.getCooldowns().addCooldown(this, 10);
                return InteractionResultHolder.fail(stack);
            }

            if (!tryConsumeEnergy(player, stack, modules, true)) {
                if (level.isClientSide()) {
                    Minecraft.getInstance().gui.getChat().addMessage(new TranslatableComponent(Constants.MESSAGE_NOT_ENOUGH_ENERGY), Constants.CHAT_LINE_ID);
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
        return Constants.SCAN_COMPUTE_DURATION;
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

    static int getModuleEnergyCost(final Player player, final ItemStack stack) {
        final LazyOptional<ScannerModule> module = stack.getCapability(Capabilities.SCANNER_MODULE_CAPABILITY);
        return module.map(p -> p.getEnergyCost(player, stack)).orElse(0);
    }

    private static boolean tryConsumeEnergy(final Player player, final ItemStack scanner, final List<ItemStack> modules, final boolean simulate) {
        if (!Settings.useEnergy) {
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
            totalCostAccumulator += getModuleEnergyCost(player, module);
        }
        final int totalCost = totalCostAccumulator;

        final int extracted = energyStorage.map(storage -> storage.extractEnergy(totalCost, simulate)).orElse(0);
        if (extracted < totalCost) {
            return false;
        }

        return true;
    }

    private static boolean collectModules(final ItemStack scanner, final List<ItemStack> modules) {
        final LazyOptional<IItemHandler> itemHandler = scanner.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
        return itemHandler
                .filter(handler -> handler instanceof ItemHandlerScanner)
                .map(handler -> {
                    final IItemHandler activeModules = ((ItemHandlerScanner) handler).getActiveModules();
                    boolean hasScannerModules = false;
                    for (int slot = 0; slot < activeModules.getSlots(); slot++) {
                        final ItemStack module = activeModules.getStackInSlot(slot);
                        if (module.isEmpty()) {
                            continue;
                        }

                        modules.add(module);

                        final LazyOptional<ScannerModule> capability = module.getCapability(Capabilities.SCANNER_MODULE_CAPABILITY);
                        hasScannerModules |= capability.map(ScannerModule::hasResultProvider).orElse(false);
                    }

                    return hasScannerModules;
                })
                .orElse(false);
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
