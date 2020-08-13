package li.cil.scannable.common.item;

import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.config.Settings;
import li.cil.scannable.common.scanning.ScannerModuleBlockConfigurable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public final class ItemScannerModuleBlockConfigurable extends AbstractItemScannerModuleBlock {
    private static final String TAG_BLOCK = "block";

    public static Optional<Block> getBlock(final ItemStack stack) {
        final CompoundNBT nbt = stack.getTag();
        if (nbt == null || !nbt.contains(TAG_BLOCK, NBT.TAG_STRING)) {
            return Optional.empty();
        }

        final ResourceLocation registryName = new ResourceLocation(nbt.getString(TAG_BLOCK));
        final Block block = ForgeRegistries.BLOCKS.getValue(registryName);
        if (block == null || block == Blocks.AIR) {
            return Optional.empty();
        }

        return Optional.of(block);
    }

    public static void setBlock(final ItemStack stack, final Block block) {
        final CompoundNBT nbt = stack.getOrCreateTag();

        final ResourceLocation registryName = block.getRegistryName();
        if (registryName == null) {
            return;
        }

        nbt.putString(TAG_BLOCK, registryName.toString());
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
        final Optional<Block> block = getBlock(stack);
        if (!block.isPresent()) {
            tooltip.add(new TranslationTextComponent(Constants.TOOLTIP_MODULE_BLOCK));
        } else {
            tooltip.add(new TranslationTextComponent(Constants.TOOLTIP_MODULE_BLOCK_NAME, block.get().getNameTextComponent()));
        }
        super.addInformation(stack, world, tooltip, flag);
    }

    @Override
    public boolean doesSneakBypassUse(final ItemStack stack, final IWorldReader world, final BlockPos pos, final PlayerEntity player) {
        return false;
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

        setBlock(stack, block);

        return ActionResultType.SUCCESS;
    }
}
