package li.cil.scannable.common.gui;

import li.cil.scannable.client.gui.GuiScanner;
import li.cil.scannable.common.container.ContainerScanner;
import li.cil.scannable.common.init.Items;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

public class GuiHandlerCommon implements IGuiHandler {
    @Nullable
    @Override
    public Object getServerGuiElement(final int id, final EntityPlayer player, final World world, final int x, final int y, final int z) {
        switch (GuiId.VALUES[id]) {
            case SCANNER: {
                final EnumHand hand = EnumHand.values()[x];
                final ItemStack stack = player.getHeldItem(hand);
                if (Items.isScanner(stack)) {
                    return new ContainerScanner(player, hand);
                }
                break;
            }
        }
        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(final int id, final EntityPlayer player, final World world, final int x, final int y, final int z) {
        switch (GuiId.VALUES[id]) {
            case SCANNER: {
                final EnumHand hand = EnumHand.values()[x];
                final ItemStack stack = player.getHeldItem(hand);
                if (Items.isScanner(stack)) {
                    return new GuiScanner(new ContainerScanner(player, hand));
                }
                break;
            }
        }
        return null;
    }
}
