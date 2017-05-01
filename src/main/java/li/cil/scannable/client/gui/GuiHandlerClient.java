package li.cil.scannable.client.gui;

import li.cil.scannable.common.gui.GuiHandlerCommon;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public final class GuiHandlerClient extends GuiHandlerCommon {
    @Nullable
    @Override
    public Object getClientGuiElement(final int id, final EntityPlayer player, final World world, final int x, final int y, final int z) {
        return super.getClientGuiElement(id, player, world, x, y, z);
    }
}
