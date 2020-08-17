package li.cil.scannable.common.item;

import li.cil.scannable.api.API;
import li.cil.scannable.api.scanning.ScannerModule;
import li.cil.scannable.client.ScanManager;
import li.cil.scannable.client.audio.SoundManager;
import li.cil.scannable.common.capabilities.CapabilityProviderScanner;
import li.cil.scannable.common.capabilities.CapabilityScannerModule;
import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.config.Settings;
import li.cil.scannable.common.container.ScannerContainerProvider;
import li.cil.scannable.common.inventory.ItemHandlerScanner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@ObjectHolder(API.MOD_ID)
public final class ItemScanner extends AbstractItem {
    @ObjectHolder(Constants.NAME_SCANNER)
    public static final Item INSTANCE = null;

    public static boolean isScanner(final ItemStack stack) {
        return stack.getItem() == INSTANCE;
    }

    // --------------------------------------------------------------------- //

    public ItemScanner() {
        super(new Properties().maxStackSize(1));
    }

    // --------------------------------------------------------------------- //
    // Item

    @Override
    public ICapabilityProvider initCapabilities(final ItemStack stack, @Nullable final CompoundNBT nbt) {
        return new CapabilityProviderScanner(stack);
    }

    @Override
    public void fillItemGroup(final ItemGroup group, final NonNullList<ItemStack> items) {
        super.fillItemGroup(group, items);

        if (!isInGroup(group)) {
            return;
        }

        if (group == ItemGroup.SEARCH) {
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
    public void addInformation(final ItemStack stack, @Nullable final World world, final List<ITextComponent> tooltip, final ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);

        tooltip.add(new TranslationTextComponent(Constants.TOOLTIP_SCANNER));

        if (world == null) {
            return; // Presumably from initial search tree population where capabilities have not yet been initialized.
        }

        if (!Settings.useEnergy) {
            return;
        }

        final LazyOptional<IEnergyStorage> energyStorage = stack.getCapability(CapabilityEnergy.ENERGY);
        if (!energyStorage.isPresent()) {
            return;
        }

        energyStorage.ifPresent(storage -> {
            tooltip.add(new TranslationTextComponent(Constants.TOOLTIP_SCANNER_ENERGY, storage.getEnergyStored(), storage.getMaxEnergyStored()));
        });
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
        return energyStorage
                .map(storage -> 1 - storage.getEnergyStored() / (float) storage.getMaxEnergyStored())
                .orElse(1.0f);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(final World world, final PlayerEntity player, final Hand hand) {
        final ItemStack stack = player.getHeldItem(hand);
        if (player.isSneaking()) {
            if (!world.isRemote) {
                final INamedContainerProvider containerProvider = new ScannerContainerProvider(player, hand);
                NetworkHooks.openGui((ServerPlayerEntity) player, containerProvider, buffer -> buffer.writeEnumValue(hand));
            }
        } else {
            final List<ItemStack> modules = new ArrayList<>();
            if (!collectModules(stack, modules)) {
                if (world.isRemote) {
                    Minecraft.getInstance().ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TranslationTextComponent(Constants.MESSAGE_NO_SCAN_MODULES), Constants.CHAT_LINE_ID);
                }
                player.getCooldownTracker().setCooldown(this, 10);
                return ActionResult.resultFail(stack);
            }

            if (!tryConsumeEnergy(player, stack, modules, true)) {
                if (world.isRemote) {
                    Minecraft.getInstance().ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TranslationTextComponent(Constants.MESSAGE_NOT_ENOUGH_ENERGY), Constants.CHAT_LINE_ID);
                }
                player.getCooldownTracker().setCooldown(this, 10);
                return ActionResult.resultFail(stack);
            }

            player.setActiveHand(hand);
            if (world.isRemote) {
                ScanManager.INSTANCE.beginScan(player, modules);
                SoundManager.INSTANCE.playChargingSound();
            }
        }
        return ActionResult.resultSuccess(stack);
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
        if (entity.getEntityWorld().isRemote) {
            ScanManager.INSTANCE.updateScan(entity, false);
        }
    }

    @Override
    public void onPlayerStoppedUsing(final ItemStack stack, final World world, final LivingEntity entity, final int timeLeft) {
        if (world.isRemote) {
            ScanManager.INSTANCE.cancelScan();
            SoundManager.INSTANCE.stopChargingSound();
        }
        super.onPlayerStoppedUsing(stack, world, entity, timeLeft);
    }

    @Override
    public ItemStack onItemUseFinish(final ItemStack stack, final World world, final LivingEntity entity) {
        if (!(entity instanceof PlayerEntity)) {
            return stack;
        }

        if (world.isRemote) {
            SoundCanceler.cancelEquipSound();
        }

        final List<ItemStack> modules = new ArrayList<>();
        if (!collectModules(stack, modules)) {
            return stack;
        }

        final boolean hasEnergy = tryConsumeEnergy((PlayerEntity) entity, stack, modules, false);
        if (world.isRemote) {
            SoundManager.INSTANCE.stopChargingSound();

            if (hasEnergy) {
                ScanManager.INSTANCE.updateScan(entity, true);
                SoundManager.INSTANCE.playActivateSound();
            } else {
                ScanManager.INSTANCE.cancelScan();
            }
        }

        final PlayerEntity player = (PlayerEntity) entity;
        player.getCooldownTracker().setCooldown(this, 40);

        return stack;
    }

    // --------------------------------------------------------------------- //

    static int getModuleEnergyCost(final PlayerEntity player, final ItemStack stack) {
        final LazyOptional<ScannerModule> module = stack.getCapability(CapabilityScannerModule.SCANNER_MODULE_CAPABILITY);
        return module.map(p -> p.getEnergyCost(player, stack)).orElse(0);
    }

    private static boolean tryConsumeEnergy(final PlayerEntity player, final ItemStack scanner, final List<ItemStack> modules, final boolean simulate) {
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

                        final LazyOptional<ScannerModule> capability = module.getCapability(CapabilityScannerModule.SCANNER_MODULE_CAPABILITY);
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
            if (event.getSound() == SoundEvents.ITEM_ARMOR_EQUIP_GENERIC) {
                event.setCanceled(true);
            }
            MinecraftForge.EVENT_BUS.unregister(this);
        }
    }
}
