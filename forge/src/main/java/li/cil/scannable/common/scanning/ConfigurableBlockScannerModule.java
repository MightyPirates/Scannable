package li.cil.scannable.common.scanning;

import li.cil.scannable.api.scanning.BlockScannerModule;
import li.cil.scannable.api.scanning.ScanResultProvider;
import li.cil.scannable.client.scanning.ScanResultProviders;
import li.cil.scannable.client.scanning.filter.BlockCacheScanFilter;
import li.cil.scannable.common.config.CommonConfig;
import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.item.ConfigurableBlockScannerModuleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.function.Predicate;

public enum ConfigurableBlockScannerModule implements BlockScannerModule {
    INSTANCE;

    @Override
    public int getEnergyCost(final ItemStack module) {
        return CommonConfig.energyCostModuleBlock;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ScanResultProvider getResultProvider() {
        return ScanResultProviders.BLOCKS.get();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float adjustLocalRange(final float range) {
        return range * Constants.BLOCK_MODULE_RADIUS_MULTIPLIER;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Predicate<BlockState> getFilter(final ItemStack module) {
        final List<Block> blocks = ConfigurableBlockScannerModuleItem.getBlocks(module);
        return new BlockCacheScanFilter(blocks);
    }
}
