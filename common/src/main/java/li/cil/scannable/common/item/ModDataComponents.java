/* SPDX-License-Identifier: MIT */

package li.cil.scannable.common.item;

import com.mojang.serialization.Codec;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import li.cil.scannable.util.RegistryUtils;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.component.ItemContainerContents;

import java.util.List;

/**
 * Item data components replacing the item NBT used before MC 1.20.5.
 */
public final class ModDataComponents {
    private static final DeferredRegister<DataComponentType<?>> COMPONENTS = RegistryUtils.get(Registries.DATA_COMPONENT_TYPE);

    // --------------------------------------------------------------------- //

    /**
     * Modules stored inside a scanner.
     */
    public static final RegistrySupplier<DataComponentType<ItemContainerContents>> MODULES = COMPONENTS.register("modules", () ->
        DataComponentType.<ItemContainerContents>builder()
            .persistent(ItemContainerContents.CODEC)
            .networkSynchronized(ItemContainerContents.STREAM_CODEC)
            .build());

    /**
     * Blocks configured on a configurable block scanner module.
     */
    public static final RegistrySupplier<DataComponentType<List<Identifier>>> BLOCKS = COMPONENTS.register("blocks", ModDataComponents::resourceLocationList);

    /**
     * Entity types configured on a configurable entity scanner module.
     */
    public static final RegistrySupplier<DataComponentType<List<Identifier>>> ENTITY_TYPES = COMPONENTS.register("entity_types", ModDataComponents::resourceLocationList);

    /**
     * Set on configurable modules that may not be reconfigured.
     */
    public static final RegistrySupplier<DataComponentType<Boolean>> LOCKED = COMPONENTS.register("locked", () ->
        DataComponentType.<Boolean>builder()
            .persistent(Codec.BOOL)
            .networkSynchronized(ByteBufCodecs.BOOL)
            .build());

    /**
     * Energy stored in a scanner. Only used on NeoForge; on Fabric the energy API
     * brings its own component.
     */
    public static final RegistrySupplier<DataComponentType<Integer>> ENERGY = COMPONENTS.register("energy", () ->
        DataComponentType.<Integer>builder()
            .persistent(Codec.INT)
            .networkSynchronized(ByteBufCodecs.VAR_INT)
            .build());

    // --------------------------------------------------------------------- //

    public static void initialize() {
    }

    // --------------------------------------------------------------------- //

    private static DataComponentType<List<Identifier>> resourceLocationList() {
        return DataComponentType.<List<Identifier>>builder()
            .persistent(Identifier.CODEC.listOf())
            .networkSynchronized(Identifier.STREAM_CODEC.apply(ByteBufCodecs.list()))
            .build();
    }

    private ModDataComponents() {
    }
}
