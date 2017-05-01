package li.cil.scannable.common;

import li.cil.scannable.api.API;
import li.cil.scannable.common.capabilities.CapabilityScanResultProvider;
import li.cil.scannable.common.init.Items;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.function.Supplier;

/**
 * Takes care of common setup.
 */
public class ProxyCommon {
    public void onPreInit(final FMLPreInitializationEvent event) {
        // Initialize capabilities.
        CapabilityScanResultProvider.register();

        // Register blocks and items.
        Items.register(this);
    }

    public void onInit(final FMLInitializationEvent event) {
        // Hardcoded recipes!
        Items.addRecipes();
    }

    public void onPostInit(final FMLPostInitializationEvent event) {
    }

    // --------------------------------------------------------------------- //

    public Item registerItem(final String name, final Supplier<Item> constructor) {
        final Item item = constructor.get().
                setUnlocalizedName(API.MOD_ID + "." + name).
                setCreativeTab(CreativeTabs.TOOLS).
                setRegistryName(name);
        GameRegistry.register(item);
        return item;
    }
}
