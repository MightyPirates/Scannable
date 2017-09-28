package li.cil.scannable.common.item;

import li.cil.scannable.common.Scannable;
import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.config.Settings;
import li.cil.scannable.common.init.Items;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class ItemScannerModuleBlockConfigurable extends AbstractItemScannerModuleBlock {
    private static final String TAG_BLOCK = "block";
    private static final String TAG_METADATA = "meta";

    private static final Set<IBlockState> loggedWarningFor = Collections.synchronizedSet(new HashSet<>());

    @SuppressWarnings("deprecation")
    @Nullable
    public static IBlockState getBlockState(final ItemStack stack) {
        if (!Items.isModuleBlock(stack)) {
            return null;
        }

        final NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null || !nbt.hasKey(TAG_BLOCK, NBT.TAG_STRING) || !nbt.hasKey(TAG_METADATA, NBT.TAG_INT)) {
            return null;
        }

        final ResourceLocation blockName = new ResourceLocation(nbt.getString(TAG_BLOCK));
        final Block block = ForgeRegistries.BLOCKS.getValue(blockName);
        if (block == null || block == Blocks.AIR) {
            return null;
        }

        final int blockMeta = nbt.getInteger(TAG_METADATA);
        return block.getStateFromMeta(blockMeta);
    }

    private static void setBlockState(final ItemStack stack, final IBlockState state) {
        final NBTTagCompound nbt;
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(nbt = new NBTTagCompound());
        } else {
            nbt = stack.getTagCompound();
        }

        assert nbt != null;

        final ResourceLocation blockName = state.getBlock().getRegistryName();
        if (blockName == null) {
            return;
        }

        nbt.setString(TAG_BLOCK, blockName.toString());
        nbt.setInteger(TAG_METADATA, state.getBlock().getMetaFromState(state));
    }

    // --------------------------------------------------------------------- //
    // Item

    @Override
    public void addInformation(final ItemStack stack, final EntityPlayer player, final List<String> tooltip, final boolean advanced) {
        final IBlockState state = getBlockState(stack);
        if (state == null) {
            tooltip.add(I18n.format(Constants.TOOLTIP_MODULE_BLOCK));
        } else {
            final ItemStack blockStack = getItemStackFromState(state, player);
            if (!blockStack.isEmpty()) {
                tooltip.add(I18n.format(Constants.TOOLTIP_MODULE_BLOCK_NAME, blockStack.getDisplayName()));
            } else {
                tooltip.add(I18n.format(Constants.TOOLTIP_MODULE_BLOCK_NAME, state.getBlock().getLocalizedName()));
            }
        }
        super.addInformation(stack, player, tooltip, advanced);
    }

    @Override
    public boolean doesSneakBypassUse(final ItemStack stack, final IBlockAccess world, final BlockPos pos, final EntityPlayer player) {
        return false;
    }

    @Override
    public EnumActionResult onItemUse(final ItemStack stack, final EntityPlayer player, final World world, final BlockPos pos, final EnumHand hand, final EnumFacing facing, final float hitX, final float hitY, final float hitZ) {
        if (!world.isBlockLoaded(pos)) {
            return EnumActionResult.PASS;
        }
        if (world.isAirBlock(pos)) {
            return EnumActionResult.PASS;
        }

        final IBlockState state = world.getBlockState(pos).getActualState(world, pos);

        if (Settings.getBlockBlacklistSet().contains(state.getBlock())) {
            if (world.isRemote) {
                Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentTranslation(Constants.MESSAGE_BLOCK_BLACKLISTED), Constants.CHAT_LINE_ID);
            }
            player.getCooldownTracker().setCooldown(this, 10);
            return EnumActionResult.SUCCESS;
        }

        setBlockState(stack, state);

        return EnumActionResult.SUCCESS;
    }

    // --------------------------------------------------------------------- //

    @SuppressWarnings("deprecation")
    private static ItemStack getItemStackFromState(final IBlockState state, final EntityPlayer player) {
        final Block block = state.getBlock();
        try {
            return block.getPickBlock(state, null, player.getEntityWorld(), BlockPos.ORIGIN, null);
        } catch (final Throwable t) {
            try {
                final Item item = Item.getItemFromBlock(block);
                final int damage = block.damageDropped(state);
                final ItemStack stack = new ItemStack(item, 1, damage);
                final int meta = item.getMetadata(stack);
                if (Objects.equals(block.getStateFromMeta(meta), state)) {
                    return stack;
                } else {
                    throw new Exception("Block/Item implementation does not allow round-trip via Block.damageDropped/Item.getMetadata/Block.getStateFromMeta: " + block.toString() + ", " + item.toString());
                }
            } catch (final Throwable t2) {
                if (loggedWarningFor.add(state)) {
                    // Log twice to get both stack traces. Don't log first trace if second lookup succeeds.
                    Scannable.getLog().warn("Failed determining dropped block for " + state.toString() + " via getPickBlock, trying to resolve via meta.", t);
                    Scannable.getLog().error("Failed determining dropped block for " + state.toString() + " via meta, clearing bedrock ore.", t2);
                }
            }
        }
        return ItemStack.EMPTY;
    }
}
