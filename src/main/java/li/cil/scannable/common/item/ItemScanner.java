package li.cil.scannable.common.item;

import li.cil.scannable.api.scanning.ScanResultProvider;
import li.cil.scannable.client.ScanManager;
import li.cil.scannable.common.Scannable;
import li.cil.scannable.common.capabilities.CapabilityProviderItemScanner;
import li.cil.scannable.common.capabilities.CapabilityScanResultProvider;
import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.config.Settings;
import li.cil.scannable.common.gui.GuiId;
import li.cil.scannable.common.init.Items;
import li.cil.scannable.common.inventory.ItemHandlerScanner;
import li.cil.scannable.util.SoundManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public final class ItemScanner extends Item {
    public ItemScanner() {
        setMaxStackSize(1);
    }

    // --------------------------------------------------------------------- //
    // Item

    @Override
    public ICapabilityProvider initCapabilities(final ItemStack stack, @Nullable final NBTTagCompound nbt) {
        return new CapabilityProviderItemScanner(stack);
    }

    @Override
    public void getSubItems(final CreativeTabs tab, final NonNullList<ItemStack> items) {
        super.getSubItems(tab, items);

        if (!isInCreativeTab(tab)) {
            return;
        }

        final ItemStack stack = new ItemStack(this);
        final IEnergyStorage energyStorage = stack.getCapability(CapabilityEnergy.ENERGY, null);
        if (energyStorage == null) {
            return;
        }

        energyStorage.receiveEnergy(energyStorage.getMaxEnergyStored(), false);
        items.add(stack);
    }

    @Override
    public void addInformation(final ItemStack stack, @Nullable final World world, final List<String> tooltip, final ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);

        tooltip.add(I18n.format(Constants.TOOLTIP_SCANNER));

        if (!Settings.useEnergy()) {
            return;
        }

        final IEnergyStorage energyStorage = stack.getCapability(CapabilityEnergy.ENERGY, null);
        if (energyStorage == null) {
            return;
        }

        tooltip.add(I18n.format(Constants.TOOLTIP_SCANNER_ENERGY, energyStorage.getEnergyStored(), energyStorage.getMaxEnergyStored()));
    }

    @Override
    public boolean showDurabilityBar(final ItemStack stack) {
        return Settings.useEnergy();
    }

    @Override
    public double getDurabilityForDisplay(final ItemStack stack) {
        if (!Settings.useEnergy()) {
            return 0;
        }

        final IEnergyStorage energyStorage = stack.getCapability(CapabilityEnergy.ENERGY, null);
        if (energyStorage == null) {
            return 1;
        }

        return 1 - energyStorage.getEnergyStored() / (float) energyStorage.getMaxEnergyStored();
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(final World world, final EntityPlayer player, final EnumHand hand) {
        final ItemStack stack = player.getHeldItem(hand);
        if (player.isSneaking()) {
            player.openGui(Scannable.instance, GuiId.SCANNER.id, world, hand.ordinal(), 0, 0);
        } else {
            final List<ItemStack> modules = new ArrayList<>();
            if (!collectModules(stack, modules)) {
                if (world.isRemote) {
                    Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentTranslation(Constants.MESSAGE_NO_SCAN_MODULES), Constants.CHAT_LINE_ID);
                }
                player.getCooldownTracker().setCooldown(this, 10);
                return new ActionResult<>(EnumActionResult.FAIL, stack);
            }

            if (!tryConsumeEnergy(player, stack, modules, true)) {
                if (world.isRemote) {
                    Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentTranslation(Constants.MESSAGE_NOT_ENOUGH_ENERGY), Constants.CHAT_LINE_ID);
                }
                player.getCooldownTracker().setCooldown(this, 10);
                return new ActionResult<>(EnumActionResult.FAIL, stack);
            }

            player.setActiveHand(hand);
            if (world.isRemote) {
                ScanManager.INSTANCE.beginScan(player, modules);
                SoundManager.INSTANCE.playChargingSound();
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public boolean shouldCauseReequipAnimation(final ItemStack oldStack, final ItemStack newStack, final boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem() || slotChanged;
    }

    @Override
    public int getMaxItemUseDuration(final ItemStack stack) {
        return Constants.SCAN_COMPUTE_DURATION;
    }

    @Override
    public void onUsingTick(final ItemStack stack, final EntityLivingBase entity, final int count) {
        if (entity.getEntityWorld().isRemote) {
            ScanManager.INSTANCE.updateScan(entity, false);
        }
    }

    @Override
    public void onPlayerStoppedUsing(final ItemStack stack, final World world, final EntityLivingBase entity, final int timeLeft) {
        if (world.isRemote) {
            ScanManager.INSTANCE.cancelScan();
            SoundManager.INSTANCE.stopChargingSound();
        }
        super.onPlayerStoppedUsing(stack, world, entity, timeLeft);
    }

    @Override
    public ItemStack onItemUseFinish(final ItemStack stack, final World world, final EntityLivingBase entity) {
        if (!(entity instanceof EntityPlayer)) {
            return stack;
        }

        MinecraftForge.EVENT_BUS.register(SoundCanceler.INSTANCE);

        final List<ItemStack> modules = new ArrayList<>();
        if (!collectModules(stack, modules)) {
            return stack;
        }

        final boolean hasEnergy = tryConsumeEnergy((EntityPlayer) entity, stack, modules, false);
        if (world.isRemote) {
            SoundManager.INSTANCE.stopChargingSound();

            if (hasEnergy) {
                ScanManager.INSTANCE.updateScan(entity, true);
                SoundManager.INSTANCE.playActivateSound();
            } else {
                ScanManager.INSTANCE.cancelScan();
            }
        }

        final EntityPlayer player = (EntityPlayer) entity;
        player.getCooldownTracker().setCooldown(this, 40);

        return stack;
    }

    // --------------------------------------------------------------------- //

    static int getModuleEnergyCost(final EntityPlayer player, final ItemStack module) {
        final ScanResultProvider provider = module.getCapability(CapabilityScanResultProvider.SCAN_RESULT_PROVIDER_CAPABILITY, null);
        if (provider != null) {
            return provider.getEnergyCost(player, module);
        }

        if (Items.isModuleRange(module)) {
            return Settings.getEnergyCostModuleRange();
        }

        return 0;
    }

    private static boolean tryConsumeEnergy(final EntityPlayer player, final ItemStack stack, final List<ItemStack> modules, final boolean simulate) {
        if (!Settings.useEnergy()) {
            return true;
        }

        if (player.isCreative()) {
            return true;
        }

        final IEnergyStorage energyStorage = stack.getCapability(CapabilityEnergy.ENERGY, null);
        if (energyStorage == null) {
            return false;
        }

        int totalCost = 0;
        for (final ItemStack module : modules) {
            totalCost += getModuleEnergyCost(player, module);
        }

        final int extracted = energyStorage.extractEnergy(totalCost, simulate);
        if (extracted < totalCost) {
            return false;
        }

        return true;
    }

    private static boolean collectModules(final ItemStack stack, final List<ItemStack> modules) {
        boolean hasProvider = false;
        final IItemHandler itemHandler = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        assert itemHandler instanceof ItemHandlerScanner;
        final IItemHandler activeModules = ((ItemHandlerScanner) itemHandler).getActiveModules();
        for (int slot = 0; slot < activeModules.getSlots(); slot++) {
            final ItemStack module = activeModules.getStackInSlot(slot);
            if (module.isEmpty()) {
                continue;
            }

            modules.add(module);
            if (module.hasCapability(CapabilityScanResultProvider.SCAN_RESULT_PROVIDER_CAPABILITY, null)) {
                hasProvider = true;
            }
        }
        return hasProvider;
    }

    // --------------------------------------------------------------------- //

    private enum SoundCanceler {
        INSTANCE;

        @SubscribeEvent
        public void onPlaySoundAtEntityEvent(final PlaySoundAtEntityEvent event) {
            // Suppress the re-equip sound after finishing a scan.
            if (event.getSound() == SoundEvents.ITEM_ARMOR_EQUIP_GENERIC) {
                event.setCanceled(true);
            }
            MinecraftForge.EVENT_BUS.unregister(this);
        }
    }
}
