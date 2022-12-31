package li.cil.scannable.common.item;

import dev.architectury.registry.menu.MenuRegistry;
import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.config.Strings;
import li.cil.scannable.common.container.EntityModuleContainerMenu;
import li.cil.scannable.common.scanning.ConfigurableEntityScannerModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class ConfigurableEntityScannerModuleItem extends ScannerModuleItem {
    private static final String TAG_ENTITY_DEPRECATED = "entity";
    private static final String TAG_ENTITIES = "entities";
    private static final String TAG_IS_LOCKED = "isLocked";

    public static boolean isLocked(final ItemStack stack) {
        final CompoundTag tag = stack.getTag();
        return tag != null && tag.getBoolean(TAG_IS_LOCKED);
    }

    public static List<EntityType<?>> getEntityTypes(final ItemStack stack) {
        final CompoundTag tag = stack.getTag();
        if (tag == null || !(tag.contains(TAG_ENTITY_DEPRECATED, Tag.TAG_STRING) || tag.contains(TAG_ENTITIES, Tag.TAG_LIST))) {
            return Collections.emptyList();
        }

        upgradeData(tag);

        final ListTag list = tag.getList(TAG_ENTITIES, Tag.TAG_STRING);
        final List<EntityType<?>> result = new ArrayList<>();
        list.forEach(item -> {
            final Optional<EntityType<?>> entityType = EntityType.byString(item.getAsString());
            entityType.ifPresent(result::add);
        });

        return result;
    }

    private static boolean addEntityType(final ItemStack stack, final EntityType<?> entityType) {
        final Optional<ResourceKey<EntityType<?>>> registryName = BuiltInRegistries.ENTITY_TYPE.getResourceKey(entityType);
        if (registryName.isEmpty()) {
            return false;
        }

        final CompoundTag tag = stack.getOrCreateTag();
        if (tag.getBoolean(TAG_IS_LOCKED)) {
            return false;
        }

        final StringTag itemNbt = StringTag.valueOf(registryName.get().location().toString());

        final ListTag list = tag.getList(TAG_ENTITIES, Tag.TAG_STRING);
        if (list.contains(itemNbt)) {
            return true;
        }
        if (list.size() >= Constants.CONFIGURABLE_MODULE_SLOTS) {
            return false;
        }

        // getList may have just created a new empty list.
        tag.put(TAG_ENTITIES, list);

        list.add(itemNbt);
        return true;
    }

    public static void setEntityTypeAt(final ItemStack stack, final int index, final EntityType<?> entityType) {
        if (index < 0 || index >= Constants.CONFIGURABLE_MODULE_SLOTS) {
            return;
        }

        final Optional<ResourceKey<EntityType<?>>> registryName = BuiltInRegistries.ENTITY_TYPE.getResourceKey(entityType);
        if (registryName.isEmpty()) {
            return;
        }

        final CompoundTag tag = stack.getOrCreateTag();
        if (tag.getBoolean(TAG_IS_LOCKED)) {
            return;
        }

        final StringTag itemNbt = StringTag.valueOf(registryName.get().location().toString());

        final ListTag list = tag.getList(TAG_ENTITIES, Tag.TAG_STRING);
        final int oldIndex = list.indexOf(itemNbt);
        if (oldIndex == index) {
            return;
        }

        if (index >= list.size()) {
            list.add(itemNbt);
        } else {
            list.set(index, itemNbt);
        }

        if (oldIndex >= 0) {
            list.remove(oldIndex);
        }

        tag.put(TAG_ENTITIES, list);
    }

    public static void removeEntityTypeAt(final ItemStack stack, final int index) {
        if (index < 0 || index >= Constants.CONFIGURABLE_MODULE_SLOTS) {
            return;
        }

        final CompoundTag tag = stack.getOrCreateTag();
        if (tag.getBoolean(TAG_IS_LOCKED)) {
            return;
        }

        final ListTag list = tag.getList(TAG_ENTITIES, Tag.TAG_STRING);
        if (index < list.size()) {
            list.remove(index);
        }
    }

    private static void upgradeData(final CompoundTag tag) {
        if (tag.contains(TAG_ENTITY_DEPRECATED, Tag.TAG_STRING)) {
            final ListTag list = new ListTag();
            list.add(tag.get(TAG_ENTITY_DEPRECATED));
            tag.put(TAG_ENTITIES, list);
            tag.remove(TAG_ENTITY_DEPRECATED);
        }
    }

    // --------------------------------------------------------------------- //

    public ConfigurableEntityScannerModuleItem() {
        super(ConfigurableEntityScannerModule.INSTANCE);
    }

    // --------------------------------------------------------------------- //
    // Item

    @Environment(EnvType.CLIENT)
    @Override
    public void appendHoverText(final ItemStack stack, @Nullable final Level level, final List<Component> tooltip, final TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        final List<EntityType<?>> entities = getEntityTypes(stack);
        if (!entities.isEmpty()) {
            tooltip.add(Strings.TOOLTIP_ENTITIES_LIST_CAPTION);
            entities.forEach(e -> tooltip.add(Strings.listItem(e.getDescription())));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(final Level level, final Player player, final InteractionHand hand) {
        final ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            return InteractionResultHolder.pass(stack);
        }

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            MenuRegistry.openExtendedMenu(serverPlayer, new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return stack.getHoverName();
                }

                @Override
                public AbstractContainerMenu createMenu(final int id, final Inventory inventory, final Player player) {
                    return new EntityModuleContainerMenu(id, inventory, hand);
                }
            }, buffer -> buffer.writeEnum(hand));
        }

        return InteractionResultHolder.success(stack);
    }

    @Override
    public InteractionResult interactLivingEntity(final ItemStack stack, final Player player, final LivingEntity target, final InteractionHand hand) {
        // NOT adding to `stack` parameter, because that's a copy in creative mode.
        if (addEntityType(player.getItemInHand(hand), target.getType())) {
            player.swing(hand);
            player.getInventory().setChanged();
        } else {
            if (!player.level.isClientSide() && !ConfigurableEntityScannerModuleItem.isLocked(stack)) {
                player.displayClientMessage(Strings.MESSAGE_NO_FREE_SLOTS, true);
            }
        }

        // Always succeed to prevent opening item UI.
        return InteractionResult.sidedSuccess(player.level.isClientSide());
    }
}
