package li.cil.scannable.common.item;

import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.init.Items;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

import javax.annotation.Nullable;
import java.util.List;

public final class ItemScannerModuleEntity extends AbstractItemScannerModuleEntity {
    private static final String TAG_ENTITY = "entity";

    @Nullable
    public static String getEntity(final ItemStack stack) {
        if (!Items.isModuleEntity(stack)) {
            return null;
        }

        final NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null || !nbt.hasKey(TAG_ENTITY, NBT.TAG_STRING)) {
            return null;
        }

        return nbt.getString(TAG_ENTITY);
    }

    private static void setEntity(final ItemStack stack, final String entity) {
        final NBTTagCompound nbt;
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(nbt = new NBTTagCompound());
        } else {
            nbt = stack.getTagCompound();
        }

        assert nbt != null;

        nbt.setString(TAG_ENTITY, entity);
    }

    // --------------------------------------------------------------------- //
    // Item

    @Override
    public void addInformation(final ItemStack stack, @Nullable final World world, final List<String> tooltip, final ITooltipFlag flag) {
        final String entity = getEntity(stack);
        if (entity == null) {
            tooltip.add(I18n.format(Constants.TOOLTIP_MODULE_ENTITY));
        } else {
            tooltip.add(I18n.format(Constants.TOOLTIP_MODULE_ENTITY_NAME, entity));
        }
        super.addInformation(stack, world, tooltip, flag);
    }

    @Override
    public boolean doesSneakBypassUse(final ItemStack stack, final IBlockAccess world, final BlockPos pos, final EntityPlayer player) {
        return false;
    }

    @Override
    public boolean shouldCauseReequipAnimation(final ItemStack oldStack, final ItemStack newStack, final boolean slotChanged) {
        if (!slotChanged && Items.isModuleEntity(oldStack) && Items.isModuleEntity(newStack)) {
            return false;
        }
        return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
    }

    @Override
    public boolean itemInteractionForEntity(final ItemStack stack, final EntityPlayer player, final EntityLivingBase target, final EnumHand hand) {
        if (!(target instanceof EntityLiving)) {
            return false;
        }

        final String entity = EntityList.getEntityString(target);
        if (entity == null) {
            return false;
        }

        // NOT stack, because that's a copy in creative mode.
        setEntity(player.getHeldItem(hand), entity);
        player.swingArm(hand);
        player.inventory.markDirty();
        return true;
    }
}
