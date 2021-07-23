package li.cil.scannable.common.container;

import li.cil.scannable.common.Scannable;
import li.cil.scannable.common.item.ItemScannerModuleBlockConfigurable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockModuleContainer extends AbstractModuleContainer {
    public static BlockModuleContainer createForServer(final int windowId, final PlayerInventory inventory, final Hand hand) {
        return new BlockModuleContainer(windowId, inventory, hand);
    }

    public static BlockModuleContainer createForClient(final int windowId, final PlayerInventory inventory, final PacketBuffer buffer) {
        final Hand hand = buffer.readEnum(Hand.class);
        return new BlockModuleContainer(windowId, inventory, hand);
    }

    // --------------------------------------------------------------------- //

    public BlockModuleContainer(final int windowId, final PlayerInventory inventory, final Hand hand) {
        super(Scannable.BLOCK_MODULE_CONTAINER.get(), windowId, inventory, hand);
    }

    @Override
    public void removeItemAt(final int index) {
        ItemScannerModuleBlockConfigurable.removeBlockAt(getPlayer().getItemInHand(getHand()), index);
    }

    @Override
    public void setItemAt(final int index, final String value) {
        try {
            final ResourceLocation registryName = new ResourceLocation(value);
            final Block block = ForgeRegistries.BLOCKS.getValue(registryName);
            if (block != null && block != Blocks.AIR) {
                final ItemStack stack = getPlayer().getItemInHand(getHand());
                ItemScannerModuleBlockConfigurable.setBlockAt(stack, index, block);
            }
        } catch (final ResourceLocationException e) {
            Scannable.getLog().error(e);
        }
    }
}
