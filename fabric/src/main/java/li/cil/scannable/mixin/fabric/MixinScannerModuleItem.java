package li.cil.scannable.mixin.fabric;

import li.cil.scannable.api.scanning.ScannerModule;
import li.cil.scannable.api.scanning.ScannerModuleProvider;
import li.cil.scannable.common.item.ScannerModuleItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(ScannerModuleItem.class)
public abstract class MixinScannerModuleItem implements ScannerModuleProvider {
    @Shadow(remap = false)
    public abstract ScannerModule getModule();

    // --------------------------------------------------------------------- //
    // ScannerModuleProvider

    @Override
    public Optional<ScannerModule> getScannerModule(final ItemStack module) {
        if (module.is(this.asItem())) {
            return Optional.of(getModule());
        } else {
            return Optional.empty();
        }
    }
}
