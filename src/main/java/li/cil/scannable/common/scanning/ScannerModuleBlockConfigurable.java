package li.cil.scannable.common.scanning;

import li.cil.scannable.api.API;
import li.cil.scannable.api.scanning.ScanFilterBlock;
import li.cil.scannable.api.scanning.ScanResultProvider;
import li.cil.scannable.api.scanning.ScannerModuleBlock;
import li.cil.scannable.client.scanning.filter.ScanFilterSingleBlock;
import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.config.Settings;
import li.cil.scannable.common.item.ItemScannerModuleBlockConfigurable;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.Optional;

public enum ScannerModuleBlockConfigurable implements ScannerModuleBlock {
    INSTANCE;

    @Override
    public int getEnergyCost(final PlayerEntity player, final ItemStack module) {
        return Settings.energyCostModuleBlock;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ScanResultProvider getResultProvider() {
        return GameRegistry.findRegistry(ScanResultProvider.class).getValue(API.SCAN_RESULT_PROVIDER_BLOCKS);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float adjustLocalRange(final float range) {
        return range * Constants.MODULE_BLOCK_RADIUS_MULTIPLIER;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Optional<ScanFilterBlock> getFilter(final ItemStack module) {
        final Optional<Block> block = ItemScannerModuleBlockConfigurable.getBlock(module);
        return block.map(ScanFilterSingleBlock::new);
    }
}
