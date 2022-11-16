package li.cil.scannable.common.item;

import dev.architectury.registry.menu.MenuRegistry;
import li.cil.scannable.api.scanning.ScannerModule;
import li.cil.scannable.client.ScanManager;
import li.cil.scannable.client.audio.SoundManager;
import li.cil.scannable.common.config.CommonConfig;
import li.cil.scannable.common.config.Strings;
import li.cil.scannable.common.container.ScannerContainerMenu;
import li.cil.scannable.common.energy.ItemEnergyStorage;
import li.cil.scannable.common.inventory.ScannerContainer;
import li.cil.scannable.util.PlatformUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public void fillItemCategory(final CreativeModeTab group, final NonNullList<ItemStack> items) {
        super.fillItemCategory(group, items);

        if (allowedIn(group) && CommonConfig.useEnergy) {
            final ItemStack stack = new ItemStack(this);
            ItemEnergyStorage.of(stack).ifPresent(energy -> {
                energy.receiveEnergy(Integer.MAX_VALUE, false);
                items.add(stack);
            });
        }
    }

    @Override
    public void appendHoverText(final ItemStack stack, @Nullable final Level level, final List<Component> tooltip, final TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        if (CommonConfig.useEnergy) {
            ItemEnergyStorage.of(stack).ifPresent(energy ->
                tooltip.add(Strings.energyStorage(energy.getEnergyStored(), energy.getMaxEnergyStored())));
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
                        return new ScannerContainerMenu(id, inventory, hand, ScannerContainer.of(stack));
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
    public int getUseDuration(final ItemStack stack) {
        return ScanManager.SCAN_COMPUTE_DURATION;
    }

    @Override
    public void onUseTick(final Level level, final LivingEntity entity, final ItemStack stack, final int count) {
        super.onUseTick(level, entity, stack, count);
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
        if (!CommonConfig.useEnergy) {
            return 0;
        }

        return PlatformUtils.getModule(stack)
            .map(module -> module.getEnergyCost(stack)).orElse(0);
    }

    private static float getRelativeEnergy(final ItemStack stack) {
        if (!CommonConfig.useEnergy) {
            return 0;
        }

        return ItemEnergyStorage.of(stack)
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

        final Optional<ItemEnergyStorage> energyStorage = ItemEnergyStorage.of(scanner);
        if (!energyStorage.isPresent()) {
            return false;
        }

        long totalCostAccumulator = 0;
        for (final ItemStack module : modules) {
            totalCostAccumulator += getModuleEnergyCost(module);
        }
        final long totalCost = totalCostAccumulator;

        final long extracted = energyStorage.map(storage -> storage.extractEnergy(totalCost, simulate)).orElse(0L);

        return extracted >= totalCost;
    }

    private static boolean collectModules(final ItemStack scanner, final List<ItemStack> modules) {
        final ScannerContainer container = ScannerContainer.of(scanner);
        final Container activeModules = container.getActiveModules();
        boolean hasScannerModules = false;
        for (int slot = 0; slot < activeModules.getContainerSize(); slot++) {
            final ItemStack module = activeModules.getItem(slot);
            if (module.isEmpty()) {
                continue;
            }

            modules.add(module);

            hasScannerModules |= PlatformUtils.getModule(module)
                .map(ScannerModule::hasResultProvider).orElse(false);
        }
        return hasScannerModules;
    }
}
