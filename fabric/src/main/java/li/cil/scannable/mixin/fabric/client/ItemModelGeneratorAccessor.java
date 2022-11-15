package li.cil.scannable.mixin.fabric.client;

import com.google.gson.JsonElement;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

@Mixin(ItemModelGenerators.class)
public interface ItemModelGeneratorAccessor {
    @Accessor("output")
    BiConsumer<ResourceLocation, Supplier<JsonElement>> getOutput();
}
