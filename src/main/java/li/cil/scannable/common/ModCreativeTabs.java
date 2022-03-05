package li.cil.scannable.common;

import dev.architectury.registry.CreativeTabRegistry;
import li.cil.scannable.api.API;
import li.cil.scannable.common.item.Items;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public final class ModCreativeTabs {
    public static final CreativeModeTab COMMON = CreativeTabRegistry.create(new ResourceLocation(API.MOD_ID, "common"), () ->
        new ItemStack(Items.SCANNER.get())
    );
}
