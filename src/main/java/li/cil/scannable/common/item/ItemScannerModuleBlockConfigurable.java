package li.cil.scannable.common.item;

import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.container.BlockModuleContainer;
import li.cil.scannable.common.scanning.ScannerModuleBlockConfigurable;
import li.cil.scannable.common.scanning.filter.ScanFilterIgnoredBlocks;
import net.minecraft.ResourceLocationException;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fmllegacy.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ItemScannerModuleBlockConfigurable extends AbstractItemScannerModule {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final String TAG_BLOCKS = "blocks";
    private static final String TAG_IS_LOCKED = "isLocked";

    public static boolean isLocked(final ItemStack stack) {
        final CompoundTag nbt = stack.getTag();
        return nbt != null && nbt.getBoolean(TAG_IS_LOCKED);
    }

    public static List<Block> getBlocks(final ItemStack stack) {
        final CompoundTag nbt = stack.getTag();
        if (nbt == null || !nbt.contains(TAG_BLOCKS, NBT.TAG_LIST)) {
            return Collections.emptyList();
        }

        final ListTag list = nbt.getList(TAG_BLOCKS, NBT.TAG_STRING);
        final List<Block> result = new ArrayList<>();
        list.forEach(tag -> {
            try {
                final ResourceLocation registryName = new ResourceLocation(tag.getAsString());
                final Block block = ForgeRegistries.BLOCKS.getValue(registryName);
                if (block != null && block != Blocks.AIR) {
                    result.add(block);
                }
            } catch (final ResourceLocationException e) {
                LOGGER.error(e);
            }
        });

        return result;
    }

    public static boolean addBlock(final ItemStack stack, final Block block) {
        final ResourceLocation registryName = block.getRegistryName();
        if (registryName == null) {
            return false;
        }

        final CompoundTag nbt = stack.getOrCreateTag();
        if (nbt.getBoolean(TAG_IS_LOCKED)) {
            return false;
        }

        final StringTag itemNbt = StringTag.valueOf(registryName.toString());

        final ListTag list = nbt.getList(TAG_BLOCKS, NBT.TAG_STRING);
        if (list.contains(itemNbt)) {
            return true;
        }
        if (list.size() >= Constants.CONFIGURABLE_MODULE_SLOTS) {
            return false;
        }

        // getList may have just created a new empty list.
        nbt.put(TAG_BLOCKS, list);

        list.add(itemNbt);
        return true;
    }

    public static void setBlockAt(final ItemStack stack, final int index, final Block block) {
        if (index < 0 || index >= Constants.CONFIGURABLE_MODULE_SLOTS) {
            return;
        }

        final ResourceLocation registryName = block.getRegistryName();
        if (registryName == null) {
            return;
        }

        final CompoundTag nbt = stack.getOrCreateTag();
        if (nbt.getBoolean(TAG_IS_LOCKED)) {
            return;
        }

        final StringTag itemNbt = StringTag.valueOf(registryName.toString());

        final ListTag list = nbt.getList(TAG_BLOCKS, NBT.TAG_STRING);
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

    }

    public static void removeBlockAt(final ItemStack stack, final int index) {
        if (index < 0 || index >= Constants.CONFIGURABLE_MODULE_SLOTS) {
            return;
        }

        final CompoundTag nbt = stack.getOrCreateTag();
        if (nbt.getBoolean(TAG_IS_LOCKED)) {
            return;
        }

        final ListTag list = nbt.getList(TAG_BLOCKS, NBT.TAG_STRING);
        if (index < list.size()) {
            list.remove(index);
        }
    }

    // --------------------------------------------------------------------- //

    public ItemScannerModuleBlockConfigurable() {
        super(ScannerModuleBlockConfigurable.INSTANCE);
    }

    // --------------------------------------------------------------------- //
    // Item

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(final ItemStack stack, @Nullable final Level world, final List<Component> tooltip, final TooltipFlag flag) {
        final List<Block> blocks = getBlocks(stack);
        if (blocks.size() == 0) {
            tooltip.add(new TranslatableComponent(Constants.TOOLTIP_MODULE_BLOCK));
        } else {
            tooltip.add(new TranslatableComponent(Constants.TOOLTIP_MODULE_BLOCK_LIST));
            blocks.forEach(b -> tooltip.add(new TranslatableComponent(Constants.TOOLTIP_LIST_ITEM_FORMAT, b.getName())));
        }
        super.appendHoverText(stack, world, tooltip, flag);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(final Level level, final Player player, final InteractionHand hand) {
        final ItemStack stack = player.getItemInHand(hand);
        if (!player.isShiftKeyDown()) {
            if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
                NetworkHooks.openGui(serverPlayer, new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return stack.getHoverName();
                    }

                    @Override
                    public AbstractContainerMenu createMenu(final int id, final Inventory inventory, final Player player) {
                        return new BlockModuleContainer(id, inventory, hand);
                    }
                }, buffer -> buffer.writeEnum(hand));
            }
            return InteractionResultHolder.success(stack);
        }
        return InteractionResultHolder.pass(stack);
    }

    @Override
    public InteractionResult useOn(final UseOnContext context) {
        final Level world = context.getLevel();
        if (world.isEmptyBlock(context.getClickedPos())) {
            return InteractionResult.PASS;
        }

        final Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }

        final ItemStack stack = context.getItemInHand();
        final BlockState state = world.getBlockState(context.getClickedPos());

        if (ScanFilterIgnoredBlocks.shouldIgnore(state)) {
            if (world.isClientSide) {
                Minecraft.getInstance().gui.getChat().addMessage(new TranslatableComponent(Constants.MESSAGE_BLOCK_BLACKLISTED), Constants.CHAT_LINE_ID);
            }
            player.getCooldowns().addCooldown(this, 10);
            return InteractionResult.sidedSuccess(context.getLevel().isClientSide());
        }

        if (!addBlock(stack, state.getBlock())) {
            if (world.isClientSide && !ItemScannerModuleBlockConfigurable.isLocked(stack)) {
                Minecraft.getInstance().gui.getChat().addMessage(new TranslatableComponent(Constants.MESSAGE_NO_FREE_SLOTS), Constants.CHAT_LINE_ID);
            }
        }

        // Always succeed to prevent opening item UI.
        return InteractionResult.sidedSuccess(context.getLevel().isClientSide());
    }
}
