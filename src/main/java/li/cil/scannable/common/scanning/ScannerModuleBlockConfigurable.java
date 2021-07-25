package li.cil.scannable.common.scanning;

import li.cil.scannable.api.scanning.ScanFilterBlock;
import li.cil.scannable.api.scanning.ScanResultProvider;
import li.cil.scannable.api.scanning.ScannerModuleBlock;
import li.cil.scannable.client.scanning.ScanResultProviders;
import li.cil.scannable.client.scanning.filter.ScanFilterBlockCache;
import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.config.Settings;
import li.cil.scannable.common.item.ItemScannerModuleBlockConfigurable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Optional;

public enum ScannerModuleBlockConfigurable implements ScannerModuleBlock {
    INSTANCE;

    @Override
    public int getEnergyCost(final Player player, final ItemStack module) {
        return Settings.energyCostModuleBlock;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ScanResultProvider getResultProvider() {
        return ScanResultProviders.BLOCKS.get();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float adjustLocalRange(final float range) {
        return range * Constants.MODULE_BLOCK_RADIUS_MULTIPLIER;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Optional<ScanFilterBlock> getFilter(final ItemStack module) {
        final List<Block> blocks = ItemScannerModuleBlockConfigurable.getBlocks(module);
        return Optional.of(new ScanFilterBlockCache(blocks));
    }
}
