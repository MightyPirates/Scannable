package li.cil.scannable.common.item;

import li.cil.scannable.common.Scannable;
import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.config.Settings;
import li.cil.scannable.common.container.BlockModuleContainerProvider;
import li.cil.scannable.common.scanning.ScannerModuleBlockConfigurable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ItemScannerModuleBlockConfigurable extends AbstractItemScannerModuleBlock {
    private static final String TAG_BLOCK_DEPRECATED = "block";
    private static final String TAG_BLOCKS = "blocks";
    private static final String TAG_IS_LOCKED = "isLocked";

    public static boolean isLocked(final ItemStack stack) {
        final CompoundNBT nbt = stack.getTag();
        return nbt != null && nbt.getBoolean(TAG_IS_LOCKED);
    }

    public static List<Block> getBlocks(final ItemStack stack) {
        final CompoundNBT nbt = stack.getTag();
        if (nbt == null || !(nbt.contains(TAG_BLOCK_DEPRECATED, NBT.TAG_STRING) || nbt.contains(TAG_BLOCKS, NBT.TAG_LIST))) {
            return Collections.emptyList();
        }

        upgradeData(nbt);

        final ListNBT list = nbt.getList(TAG_BLOCKS, NBT.TAG_STRING);
        final List<Block> result = new ArrayList<>();
        list.forEach(tag -> {
            try {
                final ResourceLocation registryName = new ResourceLocation(tag.getString());
                final Block block = ForgeRegistries.BLOCKS.getValue(registryName);
                if (block != null && block != Blocks.AIR) {
                    result.add(block);
                }
            } catch (final ResourceLocationException e) {
                Scannable.getLog().error(e);
            }
        });

        return result;
    }

    public static boolean addBlock(final ItemStack stack, final Block block) {
        final ResourceLocation registryName = block.getRegistryName();
        if (registryName == null) {
            return false;
        }

        final CompoundNBT nbt = stack.getOrCreateTag();
        if (nbt.getBoolean(TAG_IS_LOCKED)) {
            return false;
        }

        final StringNBT itemNbt = StringNBT.valueOf(registryName.toString());

        final ListNBT list = nbt.getList(TAG_BLOCKS, NBT.TAG_STRING);
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

    public static boolean setBlockAt(final ItemStack stack, final int index, final Block block) {
        if (index < 0 || index >= Constants.CONFIGURABLE_MODULE_SLOTS) {
            return false;
        }

        final ResourceLocation registryName = block.getRegistryName();
        if (registryName == null) {
            return false;
        }

        final CompoundNBT nbt = stack.getOrCreateTag();
        if (nbt.getBoolean(TAG_IS_LOCKED)) {
            return false;
        }

        final StringNBT itemNbt = StringNBT.valueOf(registryName.toString());

        final ListNBT list = nbt.getList(TAG_BLOCKS, NBT.TAG_STRING);
        final int oldIndex = list.indexOf(itemNbt);
        if (oldIndex == index) {
            return true;
        }

        if (index >= list.size()) {
            list.add(itemNbt);
        } else {
            list.set(index, itemNbt);
        }

        if (oldIndex >= 0) {
            list.remove(oldIndex);
        }

        return true;
    }

    public static void removeBlockAt(final ItemStack stack, final int index) {
        if (index < 0 || index >= Constants.CONFIGURABLE_MODULE_SLOTS) {
            return;
        }

        final CompoundNBT nbt = stack.getOrCreateTag();
        if (nbt.getBoolean(TAG_IS_LOCKED)) {
            return;
        }

        final ListNBT list = nbt.getList(TAG_BLOCKS, NBT.TAG_STRING);
        if (index < list.size()) {
            list.remove(index);
        }
    }

    private static void upgradeData(final CompoundNBT nbt) {
        if (nbt.contains(TAG_BLOCK_DEPRECATED, NBT.TAG_STRING)) {
            final ListNBT list = new ListNBT();
            list.add(nbt.get(TAG_BLOCK_DEPRECATED));
            nbt.put(TAG_BLOCKS, list);
            nbt.remove(TAG_BLOCK_DEPRECATED);
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
    public void addInformation(final ItemStack stack, @Nullable final World world, final List<ITextComponent> tooltip, final ITooltipFlag flag) {
        final List<Block> blocks = getBlocks(stack);
        if (blocks.size() == 0) {
            tooltip.add(new TranslationTextComponent(Constants.TOOLTIP_MODULE_BLOCK));
        } else {
            tooltip.add(new TranslationTextComponent(Constants.TOOLTIP_MODULE_BLOCK_LIST));
            blocks.forEach(b -> tooltip.add(new TranslationTextComponent(Constants.TOOLTIP_LIST_ITEM_FORMAT, b.getNameTextComponent())));
        }
        super.addInformation(stack, world, tooltip, flag);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(final World world, final PlayerEntity player, final Hand hand) {
        final ItemStack stack = player.getHeldItem(hand);
        if (!player.isSneaking()) {
            if (!world.isRemote) {
                final INamedContainerProvider containerProvider = new BlockModuleContainerProvider(player, hand);
                NetworkHooks.openGui((ServerPlayerEntity) player, containerProvider, buffer -> buffer.writeEnumValue(hand));
            }
            return ActionResult.resultSuccess(stack);
        }
        return ActionResult.resultPass(stack);
    }

    @Override
    public ActionResultType onItemUse(final ItemUseContext context) {
        final World world = context.getWorld();
        if (world.isAirBlock(context.getPos())) {
            return ActionResultType.PASS;
        }

        final PlayerEntity player = context.getPlayer();
        if (player == null) {
            return ActionResultType.PASS;
        }

        final ItemStack stack = context.getItem();
        final BlockState state = world.getBlockState(context.getPos());
        final Block block = state.getBlock();

        if (Settings.shouldIgnore(block)) {
            if (world.isRemote) {
                Minecraft.getInstance().ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TranslationTextComponent(Constants.MESSAGE_BLOCK_BLACKLISTED), Constants.CHAT_LINE_ID);
            }
            player.getCooldownTracker().setCooldown(this, 10);
            return ActionResultType.SUCCESS;
        }

        if (addBlock(stack, block)) {
            return ActionResultType.SUCCESS;
        } else {
            if (world.isRemote && !ItemScannerModuleBlockConfigurable.isLocked(stack)) {
                Minecraft.getInstance().ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TranslationTextComponent(Constants.MESSAGE_NO_FREE_SLOTS), Constants.CHAT_LINE_ID);
            }
            return ActionResultType.SUCCESS; // Prevent opening item UI.
        }
    }
}
