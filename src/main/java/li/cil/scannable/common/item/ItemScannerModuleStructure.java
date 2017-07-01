package li.cil.scannable.common.item;

import li.cil.scannable.common.capabilities.CapabilityProviderModuleStructure;
import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.init.Items;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public final class ItemScannerModuleStructure extends AbstractItemScannerModule {
    private static final String TAG_HIDE_EXPLORED = "hideExplored";

    public static boolean hideExplored(final ItemStack stack) {
        if (!Items.isModuleStructure(stack)) {
            return false;
        }


        final NBTTagCompound nbt = stack.getTagCompound();
        return nbt != null && nbt.getBoolean(TAG_HIDE_EXPLORED);
    }

    private static void setHideExplored(final ItemStack stack, final boolean hideExplored) {
        final NBTTagCompound nbt;
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(nbt = new NBTTagCompound());
        } else {
            nbt = stack.getTagCompound();
        }

        assert nbt != null;

        nbt.setBoolean(TAG_HIDE_EXPLORED, hideExplored);
    }

    // --------------------------------------------------------------------- //
    // Item

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(final ItemStack stack, @Nullable final NBTTagCompound nbt) {
        return CapabilityProviderModuleStructure.INSTANCE;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(final ItemStack stack, @Nullable final World world, final List<String> tooltip, final ITooltipFlag flag) {
        tooltip.add(I18n.format(Constants.TOOLTIP_MODULE_STRUCTURE));
        if (hideExplored(stack)) {
            tooltip.add(I18n.format(Constants.TOOLTIP_MODULE_STRUCTURE_HIDE_EXPLORED));
        } else {
            tooltip.add(I18n.format(Constants.TOOLTIP_MODULE_STRUCTURE_SHOW_EXPLORED));
        }
        super.addInformation(stack, world, tooltip, flag);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(final World world, final EntityPlayer player, final EnumHand hand) {
        final ItemStack stack = player.getHeldItem(hand);

        if (!player.isSneaking()) {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }

        setHideExplored(stack, !hideExplored(stack));

        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
