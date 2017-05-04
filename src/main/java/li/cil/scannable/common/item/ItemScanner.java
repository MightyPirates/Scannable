package li.cil.scannable.common.item;

import li.cil.scannable.api.API;
import li.cil.scannable.api.scanning.ScanResultProvider;
import li.cil.scannable.client.ScanManager;
import li.cil.scannable.common.Scannable;
import li.cil.scannable.common.capabilities.CapabilityProviderItemScanner;
import li.cil.scannable.common.capabilities.CapabilityScanResultProvider;
import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.config.Settings;
import li.cil.scannable.common.gui.GuiId;
import li.cil.scannable.common.init.Items;
import li.cil.scannable.util.ItemStackUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.resources.I18n;
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public final class ItemScanner extends Item {
    private static final SoundEvent SCANNER_CHARGE = new SoundEvent(new ResourceLocation(API.MOD_ID, "scanner_charge"));
    private static final SoundEvent SCANNER_ACTIVATE = new SoundEvent(new ResourceLocation(API.MOD_ID, "scanner_activate"));

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
    public void getSubItems(final Item item, final CreativeTabs tab, final List<ItemStack> subItems) {
        super.getSubItems(item, tab, subItems);

        final ItemStack stack = new ItemStack(item);
        final IEnergyStorage energyStorage = stack.getCapability(CapabilityEnergy.ENERGY, null);
        if (energyStorage == null) {
            return;
        }

        energyStorage.receiveEnergy(energyStorage.getMaxEnergyStored(), false);
        subItems.add(stack);
    }

    @Override
    public void addInformation(final ItemStack stack, final EntityPlayer playerIn, final List<String> tooltip, final boolean advanced) {
        super.addInformation(stack, playerIn, tooltip, advanced);

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
    public ActionResult<ItemStack> onItemRightClick(final ItemStack stack, final World world, final EntityPlayer player, final EnumHand hand) {
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

        MinecraftForge.EVENT_BUS.register(this);

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

    @SubscribeEvent
    public void onPlaySoundAtEntityEvent(final PlaySoundAtEntityEvent event) {
        // Suppress the re-equip sound after finishing a scan.
        if (event.getSound() == SoundEvents.ITEM_ARMOR_EQUIP_GENERIC) {
            event.setCanceled(true);
        }
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    // --------------------------------------------------------------------- //

    static int getModuleEnergyCost(final EntityPlayer player, final ItemStack module) {
        final ScanResultProvider provider = module.getCapability(CapabilityScanResultProvider.SCAN_RESULT_PROVIDER_CAPABILITY, null);
        if (provider != null) {
            return provider.getEnergyCost(player, module);
        }

        if (Items.isModuleRange(module)) {
            return Constants.ENERGY_COST_MODULE_RANGE;
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
        final IItemHandler scannerInventory = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        assert scannerInventory != null;
        for (int slot = 0; slot < scannerInventory.getSlots(); slot++) {
            final ItemStack module = scannerInventory.getStackInSlot(slot);
            if (ItemStackUtils.isEmpty(module)) {
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

    @SideOnly(Side.CLIENT)
    private enum SoundManager {
        INSTANCE;

        @Nullable
        private PositionedSoundRecord currentChargingSound;

        void playChargingSound() {
            currentChargingSound = PositionedSoundRecord.getMasterRecord(SCANNER_CHARGE, 1);
            Minecraft.getMinecraft().getSoundHandler().playSound(currentChargingSound);
        }

        void stopChargingSound() {
            if (currentChargingSound != null) {
                Minecraft.getMinecraft().getSoundHandler().stopSound(currentChargingSound);
                currentChargingSound = null;
            }
        }

        void playActivateSound() {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SCANNER_ACTIVATE, 1));
        }
    }
}
