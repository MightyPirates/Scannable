package li.cil.scannable.client.gui;

import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.container.BlockModuleContainerMenu;
import li.cil.scannable.common.item.ConfigurableBlockScannerModuleItem;
import li.cil.scannable.common.network.Network;
import li.cil.scannable.common.network.message.SetConfiguredModuleItemAtMessage;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

public class BlockModuleScreen extends AbstractConfigurableModuleScreen<BlockModuleContainerMenu, Block> {
    public BlockModuleScreen(final BlockModuleContainerMenu container, final Inventory inventory, final Component title) {
        super(container, inventory, title, Constants.GUI_MODULE_BLOCK_LIST);
    }

    // --------------------------------------------------------------------- //

    @Override
    protected List<Block> getConfiguredItems(final ItemStack stack) {
        return ConfigurableBlockScannerModuleItem.getBlocks(stack);
    }

    @Override
    protected Component getItemName(final Block block) {
        return block.getName();
    }

    @Override
    protected void renderConfiguredItem(final Block block, final int x, final int y) {
        getMinecraft().getItemRenderer().renderGuiItem(new ItemStack(block.asItem()), x, y);
    }

    @Override
    protected void configureItemAt(final ItemStack stack, final int slot, final ItemStack value) {
        final Block block = Block.byItem(value.getItem());
        //noinspection ConstantConditions Non-null for byItem is a lie because BlockItem.getBlock can return null.
        if (block != null && block != Blocks.AIR) {
            final ResourceLocation registryName = block.getRegistryName();
            if (registryName != null) {
                Network.INSTANCE.sendToServer(new SetConfiguredModuleItemAtMessage(menu.containerId, slot, registryName));
            }
        }
    }
}
