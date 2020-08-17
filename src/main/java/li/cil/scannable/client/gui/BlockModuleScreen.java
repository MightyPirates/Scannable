package li.cil.scannable.client.gui;

import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.container.BlockModuleContainer;
import li.cil.scannable.common.item.ItemScannerModuleBlockConfigurable;
import li.cil.scannable.common.network.Network;
import li.cil.scannable.common.network.message.MessageSetConfiguredModuleItemAt;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

public class BlockModuleScreen extends AbstractConfigurableModuleScreen<BlockModuleContainer, Block> {
    public BlockModuleScreen(final BlockModuleContainer container, final PlayerInventory inventory, final ITextComponent title) {
        super(container, inventory, title, Constants.GUI_MODULE_BLOCK_LIST);
    }

    // --------------------------------------------------------------------- //

    @Override
    protected List<Block> getConfiguredItems(final ItemStack stack) {
        return ItemScannerModuleBlockConfigurable.getBlocks(stack);
    }

    @Override
    protected ITextComponent getItemName(final Block block) {
        return block.getNameTextComponent();
    }

    @Override
    protected void renderConfiguredItem(final Block block, final int x, final int y) {
        getMinecraft().getItemRenderer().renderItemIntoGUI(new ItemStack(block.asItem()), x, y);
    }

    @Override
    protected void configureItemAt(final ItemStack stack, final int slot, final ItemStack value) {
        final Block block = Block.getBlockFromItem(value.getItem());
        if (block != null && block != Blocks.AIR) {
            final ResourceLocation registryName = block.getRegistryName();
            if (registryName != null) {
                Network.INSTANCE.sendToServer(new MessageSetConfiguredModuleItemAt(container.windowId, slot, registryName.toString()));
            }
        }
    }
}
