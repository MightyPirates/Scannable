package li.cil.scannable.data.forge;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DataGenerators {
    @SubscribeEvent
    public static void gatherData(final GatherDataEvent event) {
        final DataGenerator generator = event.getGenerator();
        final ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        if (event.includeServer()) {
            final BlockTagsProvider blockTagsProvider = new ModBlockTagsProvider(generator, existingFileHelper);
            generator.addProvider(event.includeServer(), blockTagsProvider);
            generator.addProvider(event.includeServer(), new ModItemTagsProvider(generator, blockTagsProvider, existingFileHelper));
            generator.addProvider(event.includeServer(), new ModRecipeProvider(generator));
        }
        if (event.includeClient()) {
            generator.addProvider(event.includeServer(), new ModItemModelProvider(generator, existingFileHelper));
        }
    }
}
