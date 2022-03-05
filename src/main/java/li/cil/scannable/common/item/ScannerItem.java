package li.cil.scannable.common.item;

import dev.architectury.registry.menu.MenuRegistry;
import li.cil.scannable.api.scanning.ScannerModuleProvider;
import li.cil.scannable.client.ScanManager;
import li.cil.scannable.client.audio.SoundManager;
import li.cil.scannable.common.config.CommonConfig;
import li.cil.scannable.common.config.Strings;
import li.cil.scannable.common.container.ScannerContainerMenu;
import li.cil.scannable.common.inventory.ScannerItemHandler;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
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
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleBatteryItem;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public final class ScannerItem extends ModItem implements SimpleBatteryItem, FabricItem {
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
    public void fillItemCategory(final CreativeModeTab group, final NonNullList<ItemStack> items) {
        super.fillItemCategory(group, items);

        if (allowdedIn(group) && CommonConfig.useEnergy) {
            final ItemStack stack = new ItemStack(this);
            ContainerItemContext context = ContainerItemContext.withInitial(stack);
            try (Transaction transaction = Transaction.openOuter()) {
                context.find(EnergyStorage.ITEM).insert(this.getEnergyCapacity(), transaction);
                transaction.commit();
            }
            items.add(context.getItemVariant().toStack());
        }
    }

    @Override
    public void appendHoverText(final ItemStack stack, @Nullable final Level level, final List<Component> tooltip, final TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        if (CommonConfig.useEnergy) {
            EnergyStorage energyStorage = ContainerItemContext.withInitial(stack).find(EnergyStorage.ITEM);
            tooltip.add(Strings.energyStorage(energyStorage.getAmount(), energyStorage.getCapacity()));
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
                MenuRegistry.openExtendedMenu(serverPlayer, new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return stack.getHoverName();
                    }

                    @Override
                    public AbstractContainerMenu createMenu(final int id, final Inventory inventory, final Player player) {
                        return new ScannerContainerMenu(id, inventory, hand, ScannerItemHandler.of(stack));
                    }
                }, buffer -> buffer.writeEnum(hand));
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

            if (!tryConsumeEnergy(player, hand, modules, true)) {
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
    public boolean allowNbtUpdateAnimation(Player player, InteractionHand hand, final ItemStack oldStack, final ItemStack newStack) {
        return oldStack.getItem() != newStack.getItem();
    }

    @Override
    public int getUseDuration(final ItemStack stack) {
        return ScanManager.SCAN_COMPUTE_DURATION;
    }

    @Override
    public void onUseTick(final Level level, final LivingEntity entity, final ItemStack stack, final int count) {
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

        /*
        if (level.isClientSide()) {
            SoundCanceler.cancelEquipSound();
        }
         */

        final List<ItemStack> modules = new ArrayList<>();
        if (!collectModules(stack, modules)) {
            return stack;
        }

        final boolean hasEnergy = tryConsumeEnergy((Player) entity, entity.getUsedItemHand(), modules, false);
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
        if (!CommonConfig.useEnergy) {
            return 0;
        }

        if(stack.getItem() instanceof ScannerModuleProvider provider) {
            return provider.getScannerModule().getEnergyCost(stack);
        }
        return 0;
    }

    private static float getRelativeEnergy(final ItemStack stack) {
        if (!CommonConfig.useEnergy) {
            return 0;
        }

        EnergyStorage storage = ContainerItemContext.withInitial(stack).find(EnergyStorage.ITEM);
        return storage.getAmount() / (float)storage.getCapacity();
    }

    private static boolean tryConsumeEnergy(final Player player, final InteractionHand hand, final List<ItemStack> modules, final boolean simulate) {
        if (!CommonConfig.useEnergy) {
            return true;
        }

        if (player.isCreative()) {
            return true;
        }

        EnergyStorage storage = ContainerItemContext.ofPlayerHand(player, hand).find(EnergyStorage.ITEM);
        if(storage == null)
            return false;

        int totalCostAccumulator = 0;
        for (final ItemStack module : modules) {
            totalCostAccumulator += getModuleEnergyCost(module);
        }
        final int totalCost = totalCostAccumulator;
        long extracted;
        try (Transaction transaction = Transaction.openOuter()) {
            extracted = storage.extract(totalCost, transaction);
            if(!simulate)
                transaction.commit();
        }
        if (extracted < totalCost) {
            return false;
        }

        return true;
    }

    private static boolean collectModules(final ItemStack scanner, final List<ItemStack> modules) {
        ScannerItemHandler scannerItemHandler = ScannerItemHandler.of(scanner);
        if(scannerItemHandler == null)
            return false;
        boolean hasScannerModules = false;
        final NonNullList<ItemStack> activeModules = scannerItemHandler.getActiveModules();
        for (final ItemStack module : activeModules) {
            if (module.isEmpty()) {
                continue;
            }

            modules.add(module);

            boolean hasAResult = false;
            if (module.getItem() instanceof ScannerModuleProvider provider) {
                hasAResult = provider.getScannerModule().hasResultProvider();
            }
            hasScannerModules |= hasAResult;
        }
        return hasScannerModules;
    }

    @Override
    public long getEnergyCapacity() {
        return CommonConfig.energyCapacityScanner;
    }

    @Override
    public long getEnergyMaxInput() {
        return Long.MAX_VALUE;
    }

    @Override
    public long getEnergyMaxOutput() {
        return Long.MAX_VALUE;
    }

    // --------------------------------------------------------------------- //
}
