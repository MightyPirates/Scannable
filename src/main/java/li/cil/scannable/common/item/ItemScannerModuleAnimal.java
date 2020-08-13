package li.cil.scannable.common.item;

import li.cil.scannable.api.API;
import li.cil.scannable.api.scanning.ScanFilterEntity;
import li.cil.scannable.api.scanning.ScanResultProvider;
import li.cil.scannable.api.scanning.ScannerModuleEntity;
import li.cil.scannable.client.scanning.filter.ScanFilterEntityAnimal;
import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.config.Settings;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public final class ItemScannerModuleAnimal extends AbstractItemScannerModuleEntity {
    public ItemScannerModuleAnimal() {
        super(ScannerModuleAnimal.INSTANCE);
    }

    // --------------------------------------------------------------------- //
    // Item

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(final ItemStack stack, @Nullable final World world, final List<ITextComponent> tooltip, final ITooltipFlag flag) {
        tooltip.add(new TranslationTextComponent(Constants.TOOLTIP_MODULE_ANIMAL));
        super.addInformation(stack, world, tooltip, flag);
    }

    // --------------------------------------------------------------------- //

    private enum ScannerModuleAnimal implements ScannerModuleEntity {
        INSTANCE;

        @Override
        public int getEnergyCost(final PlayerEntity player, final ItemStack module) {
            return Settings.energyCostModuleAnimal;
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        public ScanResultProvider getResultProvider() {
            return GameRegistry.findRegistry(ScanResultProvider.class).getValue(API.SCAN_RESULT_PROVIDER_ENTITIES);
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        public Optional<ScanFilterEntity> getFilter(final ItemStack module) {
            return Optional.of(ScanFilterEntityAnimal.INSTANCE);
        }
    }
}
