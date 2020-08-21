package li.cil.scannable.common.item;

import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.container.EntityModuleContainerProvider;
import li.cil.scannable.common.scanning.ScannerModuleEntityConfigurable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class ItemScannerModuleEntityConfigurable extends AbstractItemScannerModuleEntity {
    private static final String TAG_ENTITY_DEPRECATED = "entity";
    private static final String TAG_ENTITIES = "entities";
    private static final String TAG_IS_LOCKED = "isLocked";

    public static boolean isLocked(final ItemStack stack) {
        final CompoundNBT nbt = stack.getTag();
        return nbt != null && nbt.getBoolean(TAG_IS_LOCKED);
    }

    public static List<EntityType<?>> getEntityTypes(final ItemStack stack) {
        final CompoundNBT nbt = stack.getTag();
        if (nbt == null || !(nbt.contains(TAG_ENTITY_DEPRECATED, NBT.TAG_STRING) || nbt.contains(TAG_ENTITIES, NBT.TAG_LIST))) {
            return Collections.emptyList();
        }

        upgradeData(nbt);

        final ListNBT list = nbt.getList(TAG_ENTITIES, NBT.TAG_STRING);
        final List<EntityType<?>> result = new ArrayList<>();
        list.forEach(tag -> {
            final Optional<EntityType<?>> entityType = EntityType.byKey(tag.getString());
            entityType.ifPresent(result::add);
        });

        return result;
    }

    private static boolean addEntityType(final ItemStack stack, final EntityType<?> entityType) {
        final ResourceLocation registryName = entityType.getRegistryName();
        if (registryName == null) {
            return false;
        }

        final CompoundNBT nbt = stack.getOrCreateTag();
        if (nbt.getBoolean(TAG_IS_LOCKED)) {
            return false;
        }

        final StringNBT itemNbt = StringNBT.valueOf(registryName.toString());

        final ListNBT list = nbt.getList(TAG_ENTITIES, NBT.TAG_STRING);
        if (list.contains(itemNbt)) {
            return true;
        }
        if (list.size() >= Constants.CONFIGURABLE_MODULE_SLOTS) {
            return false;
        }

        // getList may have just created a new empty list.
        nbt.put(TAG_ENTITIES, list);

        list.add(itemNbt);
        return true;
    }

    public static boolean setEntityTypeAt(final ItemStack stack, final int index, final EntityType<?> entityType) {
        if (index < 0 || index >= Constants.CONFIGURABLE_MODULE_SLOTS) {
            return false;
        }

        final ResourceLocation registryName = entityType.getRegistryName();
        if (registryName == null) {
            return false;
        }

        final CompoundNBT nbt = stack.getOrCreateTag();
        if (nbt.getBoolean(TAG_IS_LOCKED)) {
            return false;
        }

        final StringNBT itemNbt = StringNBT.valueOf(registryName.toString());

        final ListNBT list = nbt.getList(TAG_ENTITIES, NBT.TAG_STRING);
        final int oldIndex = list.indexOf(itemNbt);
        if (oldIndex == index) {
            return true;
        }

        if (index >= list.size()) {
            list.add(itemNbt);
        } else {
            list.set(index, itemNbt);
        }

        if (oldIndex >= 0) {
            list.remove(oldIndex);
        }

        return true;
    }

    public static void removeEntityTypeAt(final ItemStack stack, final int index) {
        if (index < 0 || index >= Constants.CONFIGURABLE_MODULE_SLOTS) {
            return;
        }

        final CompoundNBT nbt = stack.getOrCreateTag();
        if (nbt.getBoolean(TAG_IS_LOCKED)) {
            return;
        }

        final ListNBT list = nbt.getList(TAG_ENTITIES, NBT.TAG_STRING);
        if (index < list.size()) {
            list.remove(index);
        }
    }

    private static void upgradeData(final CompoundNBT nbt) {
        if (nbt.contains(TAG_ENTITY_DEPRECATED, NBT.TAG_STRING)) {
            final ListNBT list = new ListNBT();
            list.add(nbt.get(TAG_ENTITY_DEPRECATED));
            nbt.put(TAG_ENTITIES, list);
            nbt.remove(TAG_ENTITY_DEPRECATED);
        }
    }

    // --------------------------------------------------------------------- //

    public ItemScannerModuleEntityConfigurable() {
        super(ScannerModuleEntityConfigurable.INSTANCE);
    }

    // --------------------------------------------------------------------- //
    // Item

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(final ItemStack stack, @Nullable final World world, final List<ITextComponent> tooltip, final ITooltipFlag flag) {
        final List<EntityType<?>> entities = getEntityTypes(stack);
        if (entities.size() == 0) {
            tooltip.add(new TranslationTextComponent(Constants.TOOLTIP_MODULE_ENTITY));
        } else {
            tooltip.add(new TranslationTextComponent(Constants.TOOLTIP_MODULE_ENTITY_LIST));
            entities.forEach(e -> tooltip.add(new TranslationTextComponent(Constants.TOOLTIP_LIST_ITEM_FORMAT, e.getName())));
        }
        super.addInformation(stack, world, tooltip, flag);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(final World world, final PlayerEntity player, final Hand hand) {
        final ItemStack stack = player.getHeldItem(hand);
        if (!player.isSneaking()) {
            if (!world.isRemote()) {
                final INamedContainerProvider containerProvider = new EntityModuleContainerProvider(player, hand);
                NetworkHooks.openGui((ServerPlayerEntity) player, containerProvider, buffer -> buffer.writeEnumValue(hand));
            }
            return ActionResult.resultSuccess(stack);
        }
        return ActionResult.resultPass(stack);
    }

    @Override
    public ActionResultType itemInteractionForEntity(final ItemStack stack, final PlayerEntity player, final LivingEntity target, final Hand hand) {
        // NOT adding to `stack` parameter, because that's a copy in creative mode.
        if (addEntityType(player.getHeldItem(hand), target.getType())) {
            player.swingArm(hand);
            player.inventory.markDirty();
            return ActionResultType.SUCCESS;
        } else {
            if (player.getEntityWorld().isRemote && !ItemScannerModuleEntityConfigurable.isLocked(stack)) {
                Minecraft.getInstance().ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TranslationTextComponent(Constants.MESSAGE_NO_FREE_SLOTS), Constants.CHAT_LINE_ID);
            }
            return ActionResultType.SUCCESS; // Prevent opening item UI.
        }
    }
}
