package li.cil.scannable.common.item;

import dev.architectury.registry.menu.MenuRegistry;
import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.config.Strings;
import li.cil.scannable.common.container.BlockModuleContainerMenu;
import li.cil.scannable.common.scanning.ConfigurableBlockScannerModule;
import li.cil.scannable.common.scanning.filter.IgnoredBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class ConfigurableBlockScannerModuleItem extends ScannerModuleItem {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final String TAG_BLOCKS = "blocks";
    private static final String TAG_IS_LOCKED = "isLocked";

    public static boolean isLocked(final ItemStack stack) {
        final CompoundTag tag = stack.getTag();
        return tag != null && tag.getBoolean(TAG_IS_LOCKED);
    }

    public static List<Block> getBlocks(final ItemStack stack) {
        final CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(TAG_BLOCKS, Tag.TAG_LIST)) {
            return Collections.emptyList();
        }

        final ListTag list = tag.getList(TAG_BLOCKS, Tag.TAG_STRING);
        final List<Block> result = new ArrayList<>();
        list.forEach(item -> {
            try {
                final ResourceLocation registryName = new ResourceLocation(item.getAsString());
                Registry.BLOCK.getOptional(registryName).ifPresent(result::add);
            } catch (final ResourceLocationException e) {
                LOGGER.error(e);
            }
        });

        return result;
    }

    public static boolean addBlock(final ItemStack stack, final Block block) {
        final Optional<ResourceKey<Block>> registryName = Registry.BLOCK.getResourceKey(block);
        if (registryName.isEmpty()) {
            return false;
        }

        final CompoundTag tag = stack.getOrCreateTag();
        if (tag.getBoolean(TAG_IS_LOCKED)) {
            return false;
        }

        final StringTag itemNbt = StringTag.valueOf(registryName.get().location().toString());

        final ListTag list = tag.getList(TAG_BLOCKS, Tag.TAG_STRING);
        if (list.contains(itemNbt)) {
            return true;
        }
        if (list.size() >= Constants.CONFIGURABLE_MODULE_SLOTS) {
            return false;
        }

        // getList may have just created a new empty list.
        tag.put(TAG_BLOCKS, list);

        list.add(itemNbt);
        return true;
    }

    public static void setBlockAt(final ItemStack stack, final int index, final Block block) {
        if (index < 0 || index >= Constants.CONFIGURABLE_MODULE_SLOTS) {
            return;
        }

        final Optional<ResourceKey<Block>> registryName = Registry.BLOCK.getResourceKey(block);
        if (registryName.isEmpty()) {
            return;
        }

        final CompoundTag tag = stack.getOrCreateTag();
        if (tag.getBoolean(TAG_IS_LOCKED)) {
            return;
        }

        final StringTag itemNbt = StringTag.valueOf(registryName.get().location().toString());

        final ListTag list = tag.getList(TAG_BLOCKS, Tag.TAG_STRING);
        final int oldIndex = list.indexOf(itemNbt);
        if (oldIndex == index) {
            return;
        }

        if (index >= list.size()) {
            list.add(itemNbt);
        } else {
            list.set(index, itemNbt);
        }

        if (oldIndex >= 0) {
            list.remove(oldIndex);
        }

        tag.put(TAG_BLOCKS, list);
    }

    public static void removeBlockAt(final ItemStack stack, final int index) {
        if (index < 0 || index >= Constants.CONFIGURABLE_MODULE_SLOTS) {
            return;
        }

        final CompoundTag tag = stack.getOrCreateTag();
        if (tag.getBoolean(TAG_IS_LOCKED)) {
            return;
        }

        final ListTag list = tag.getList(TAG_BLOCKS, Tag.TAG_STRING);
        if (index < list.size()) {
            list.remove(index);
        }
    }

    // --------------------------------------------------------------------- //

    public ConfigurableBlockScannerModuleItem() {
        super(ConfigurableBlockScannerModule.INSTANCE);
    }

    // --------------------------------------------------------------------- //
    // Item

    @Environment(EnvType.CLIENT)
    @Override
    public void appendHoverText(final ItemStack stack, @Nullable final Level level, final List<Component> tooltip, final TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        final List<Block> blocks = getBlocks(stack);
        if (!blocks.isEmpty()) {
            tooltip.add(Strings.TOOLTIP_BLOCKS_LIST_CAPTION);
            blocks.forEach(b -> tooltip.add(Strings.listItem(b.getName())));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(final Level level, final Player player, final InteractionHand hand) {
        final ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            return InteractionResultHolder.pass(stack);
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

        return InteractionResultHolder.success(stack);
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
            player.getCooldowns().addCooldown(this, 10);
            return InteractionResult.sidedSuccess(context.getLevel().isClientSide());
        }

        if (!addBlock(stack, state.getBlock())) {
            if (!level.isClientSide() && !ConfigurableBlockScannerModuleItem.isLocked(stack)) {
                player.displayClientMessage(Strings.MESSAGE_NO_FREE_SLOTS, true);
            }
        }

        // Always succeed to prevent opening item UI.
        return InteractionResult.sidedSuccess(context.getLevel().isClientSide());
    }
}
