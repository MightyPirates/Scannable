package li.cil.scannable.common;

import li.cil.scannable.api.API;
import li.cil.scannable.client.ClientSetup;
import li.cil.scannable.client.scanning.ScanResultProviders;
import li.cil.scannable.common.config.Settings;
import li.cil.scannable.common.container.Containers;
import li.cil.scannable.common.item.Items;
import li.cil.scannable.common.tags.ItemTags;
import li.cil.scannable.util.RegistryUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(API.MOD_ID)
public final class Scannable {
    public Scannable() {
        Settings.register();

        RegistryUtils.begin();

        ItemTags.initialize();
        Items.initialize();
        Containers.initialize();

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ScanResultProviders::initialize);

        RegistryUtils.finish();

        FMLJavaModLoadingContext.get().getModEventBus().register(CommonSetup.class);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> FMLJavaModLoadingContext.get().getModEventBus().register(ClientSetup.class));
    }
}
