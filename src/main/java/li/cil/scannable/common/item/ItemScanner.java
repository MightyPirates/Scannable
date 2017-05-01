package li.cil.scannable.common.item;

import li.cil.scannable.common.Scannable;
import li.cil.scannable.common.api.ScanningAPIImpl;
import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.gui.GuiId;
import li.cil.scannable.common.inventory.ItemScannerInventory;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

public class ItemScanner extends Item {
    public ItemScanner() {
        setMaxStackSize(1);
    }

    // --------------------------------------------------------------------- //
    // Item

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(final ItemStack stack, @Nullable final NBTTagCompound nbt) {
        return new ItemScannerInventory();
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(final World world, final EntityPlayer player, final EnumHand hand) {
        if (player.isSneaking()) {
            player.openGui(Scannable.instance, GuiId.SCANNER.id, world, hand.ordinal(), 0, 0);
        } else {
            player.setActiveHand(hand);
            if (world.isRemote) {
                ScanningAPIImpl.INSTANCE.beginScan(player);
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    @Override
    public int getMaxItemUseDuration(final ItemStack stack) {
        return Constants.SCAN_COMPUTE_DURATION;
    }

    @Override
    public void onUsingTick(final ItemStack stack, final EntityLivingBase entity, final int count) {
        if (entity.getEntityWorld().isRemote) {
            ScanningAPIImpl.INSTANCE.updateScan(entity, false);
        }

        final Vec3d lookAtBase = entity.
                getPositionEyes(1).
                add(entity.getLookVec());
        final Vec3d speedBase = entity.
                getLookVec();
        for (int i = 0; i < 10; i++) {
            final Vec3d lookAt = lookAtBase.addVector(itemRand.nextGaussian(), itemRand.nextGaussian(), itemRand.nextGaussian());
            final Vec3d speed = speedBase.addVector(itemRand.nextGaussian(), itemRand.nextGaussian(), itemRand.nextGaussian());
            entity.getEntityWorld().spawnParticle(EnumParticleTypes.PORTAL, lookAt.xCoord, lookAt.yCoord, lookAt.zCoord, speed.xCoord, speed.yCoord, speed.zCoord);
        }
    }

    @Override
    public void onPlayerStoppedUsing(final ItemStack stack, final World world, final EntityLivingBase entity, final int timeLeft) {
        if (world.isRemote) {
            ScanningAPIImpl.INSTANCE.cancelScan();
        }
        super.onPlayerStoppedUsing(stack, world, entity, timeLeft);
    }

    @Override
    public ItemStack onItemUseFinish(final ItemStack stack, final World world, final EntityLivingBase entity) {
        if (world.isRemote) {
            ScanningAPIImpl.INSTANCE.updateScan(entity, true);
        }
        return stack;
    }
}
