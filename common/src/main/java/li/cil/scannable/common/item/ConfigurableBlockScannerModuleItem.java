package li.cil.scannable.common.item;

import dev.architectury.registry.menu.MenuRegistry;
import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.config.Strings;
import li.cil.scannable.common.container.BlockModuleContainerMenu;
import li.cil.scannable.common.scanning.ConfigurableBlockScannerModule;
import li.cil.scannable.common.scanning.filter.IgnoredBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public final class ConfigurableBlockScannerModuleItem extends ScannerModuleItem {
    public static boolean isLocked(final ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.LOCKED.get(), false);
    }

    public static List<Block> getBlocks(final ItemStack stack) {
        final List<Identifier> ids = getBlockIds(stack);
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }

        final List<Block> result = new ArrayList<>(ids.size());
        for (final Identifier id : ids) {
            BuiltInRegistries.BLOCK.getOptional(id).ifPresent(result::add);
        }

        return result;
    }

    public static boolean addBlock(final ItemStack stack, final Block block) {
        final Optional<ResourceKey<Block>> registryName = BuiltInRegistries.BLOCK.getResourceKey(block);
        if (registryName.isEmpty()) {
            return false;
        }

        if (isLocked(stack)) {
            return false;
        }

        final Identifier id = registryName.get().identifier();
        final List<Identifier> ids = new ArrayList<>(getBlockIds(stack));
        if (ids.contains(id)) {
            return true;
        }
        if (ids.size() >= Constants.CONFIGURABLE_MODULE_SLOTS) {
            return false;
        }

        ids.add(id);
        setBlockIds(stack, ids);
        return true;
    }

    public static void setBlockAt(final ItemStack stack, final int index, final Block block) {
        if (index < 0 || index >= Constants.CONFIGURABLE_MODULE_SLOTS) {
            return;
        }

        final Optional<ResourceKey<Block>> registryName = BuiltInRegistries.BLOCK.getResourceKey(block);
        if (registryName.isEmpty()) {
            return;
        }

        if (isLocked(stack)) {
            return;
        }

        final Identifier id = registryName.get().identifier();
        final List<Identifier> ids = new ArrayList<>(getBlockIds(stack));
        final int oldIndex = ids.indexOf(id);
        if (oldIndex == index) {
            return;
        }

        if (index >= ids.size()) {
            ids.add(id);
        } else {
            ids.set(index, id);
        }

        if (oldIndex >= 0) {
            ids.remove(oldIndex);
        }

        setBlockIds(stack, ids);
    }

    public static void removeBlockAt(final ItemStack stack, final int index) {
        if (index < 0 || index >= Constants.CONFIGURABLE_MODULE_SLOTS) {
            return;
        }

        if (isLocked(stack)) {
            return;
        }

        final List<Identifier> ids = new ArrayList<>(getBlockIds(stack));
        if (index < ids.size()) {
            ids.remove(index);
            setBlockIds(stack, ids);
        }
    }

    // --------------------------------------------------------------------- //

    private static List<Identifier> getBlockIds(final ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.BLOCKS.get(), Collections.emptyList());
    }

    private static void setBlockIds(final ItemStack stack, final List<Identifier> ids) {
        stack.set(ModDataComponents.BLOCKS.get(), List.copyOf(ids));
    }

    // --------------------------------------------------------------------- //

    public ConfigurableBlockScannerModuleItem(final Properties properties) {
        super(properties, ConfigurableBlockScannerModule.INSTANCE);
    }

    // --------------------------------------------------------------------- //
    // Item

    @Override
    public void appendHoverText(final ItemStack stack, final Item.TooltipContext context, final TooltipDisplay display, final Consumer<Component> tooltip, final TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltip, flag);

        final List<Block> blocks = getBlocks(stack);
        if (!blocks.isEmpty()) {
            tooltip.accept(Strings.TOOLTIP_BLOCKS_LIST_CAPTION);
            blocks.forEach(b -> tooltip.accept(Strings.listItem(b.getName())));
        }
    }

    @Override
    public InteractionResult use(final Level level, final Player player, final InteractionHand hand) {
        final ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            MenuRegistry.openExtendedMenu(serverPlayer, new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return stack.getHoverName();
                }

                @Override
                public AbstractContainerMenu createMenu(final int id, final Inventory inventory, final Player player) {
                    return new BlockModuleContainerMenu(id, inventory, hand);
                }
            }, buffer -> buffer.writeEnum(hand));
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useOn(final UseOnContext context) {
        final Level level = context.getLevel();
        if (level.isEmptyBlock(context.getClickedPos())) {
            return InteractionResult.PASS;
        }

        final Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }

        final ItemStack stack = context.getItemInHand();
        final BlockState state = level.getBlockState(context.getClickedPos());

        if (IgnoredBlocks.contains(state)) {
            if (!level.isClientSide()) {
                player.displayClientMessage(Strings.MESSAGE_BLOCK_IGNORED, true);
            }
            player.getCooldowns().addCooldown(stack, 10);
            return InteractionResult.SUCCESS;
        }

        if (!addBlock(stack, state.getBlock())) {
            if (!level.isClientSide() && !ConfigurableBlockScannerModuleItem.isLocked(stack)) {
                player.displayClientMessage(Strings.MESSAGE_NO_FREE_SLOTS, true);
            }
        }

        // Always succeed to prevent opening item UI.
        return InteractionResult.SUCCESS;
    }
}
