package li.cil.scannable.common;

import dev.architectury.registry.CreativeTabRegistry;
import li.cil.scannable.api.API;
import li.cil.scannable.common.config.CommonConfig;
import li.cil.scannable.common.energy.ItemEnergyStorage;
import li.cil.scannable.common.item.Items;
import li.cil.scannable.common.item.ModItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public final class ModCreativeTabs {
    public static final CreativeTabRegistry.TabSupplier COMMON = CreativeTabRegistry.create(
        new ResourceLocation(API.MOD_ID, "common"),
        builder -> {
            builder.icon(() -> new ItemStack(Items.SCANNER.get()));
            builder.displayItems((features, output, hasPermissions) -> {
                if (CommonConfig.useEnergy) {
                    final var stack = new ItemStack(Items.SCANNER.get());
                    ItemEnergyStorage.of(stack).ifPresent(energy -> {
                        energy.receiveEnergy(Integer.MAX_VALUE, false);
                        output.accept(stack);
                    });
                }

                BuiltInRegistries.ITEM.stream()
                    .filter(item -> item instanceof ModItem)
                    .forEach(item -> output.accept(new ItemStack(item)));
            });
        });

    public static void initialize() {
        // Just used to run static initializer.
    }
}
