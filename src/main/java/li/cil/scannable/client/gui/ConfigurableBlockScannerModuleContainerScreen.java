package li.cil.scannable.client.gui;

import li.cil.scannable.common.config.Strings;
import li.cil.scannable.common.container.BlockModuleContainerMenu;
import li.cil.scannable.common.item.ConfigurableBlockScannerModuleItem;
import li.cil.scannable.common.network.Network;
import li.cil.scannable.common.network.message.SetConfiguredModuleItemAtMessage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

@Environment(EnvType.CLIENT)
public class ConfigurableBlockScannerModuleContainerScreen extends AbstractConfigurableScannerModuleContainerScreen<BlockModuleContainerMenu, Block> {
    public ConfigurableBlockScannerModuleContainerScreen(final BlockModuleContainerMenu container, final Inventory inventory, final Component title) {
        super(container, inventory, title, Strings.GUI_BLOCKS_LIST_CAPTION);
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
        Minecraft.getInstance().getItemRenderer().renderGuiItem(new ItemStack(block.asItem()), x, y);
    }

    @Override
    protected void configureItemAt(final ItemStack stack, final int slot, final ItemStack value) {
        final Block block = Block.byItem(value.getItem());
        if (block != null && block != Blocks.AIR) {
            Registry.BLOCK.getResourceKey(block).ifPresent(blockResourceKey ->
                Network.sendToServer(new SetConfiguredModuleItemAtMessage(menu.containerId, slot, blockResourceKey.location())));
        }
    }
}
