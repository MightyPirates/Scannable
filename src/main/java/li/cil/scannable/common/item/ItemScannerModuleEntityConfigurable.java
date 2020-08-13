package li.cil.scannable.common.item;

import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.scanning.ScannerModuleEntityConfigurable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants.NBT;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public final class ItemScannerModuleEntityConfigurable extends AbstractItemScannerModuleEntity {
    private static final String TAG_ENTITY = "entity";

    public static Optional<EntityType<?>> getEntityType(final ItemStack stack) {
        final CompoundNBT nbt = stack.getTag();
        if (nbt == null || !nbt.contains(TAG_ENTITY, NBT.TAG_STRING)) {
            return Optional.empty();
        }

        return EntityType.byKey(nbt.getString(TAG_ENTITY));
    }

    private static void setEntityType(final ItemStack stack, final EntityType<?> entityType) {
        final ResourceLocation entityRegistryName = entityType.getRegistryName();
        if (entityRegistryName == null) {
            return;
        }

        final CompoundNBT nbt = stack.getOrCreateTag();
        nbt.putString(TAG_ENTITY, entityRegistryName.toString());
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
        final Optional<EntityType<?>> entity = getEntityType(stack);
        if (!entity.isPresent()) {
            tooltip.add(new TranslationTextComponent(Constants.TOOLTIP_MODULE_ENTITY));
        } else {
            tooltip.add(new TranslationTextComponent(Constants.TOOLTIP_MODULE_ENTITY_NAME, entity.get().getName()));
        }
        super.addInformation(stack, world, tooltip, flag);
    }

    @Override
    public boolean doesSneakBypassUse(final ItemStack stack, final IWorldReader world, final BlockPos pos, final PlayerEntity player) {
        return false;
    }

    @Override
    public boolean itemInteractionForEntity(final ItemStack stack, final PlayerEntity player, final LivingEntity target, final Hand hand) {
        // NOT stack, because that's a copy in creative mode.
        setEntityType(player.getHeldItem(hand), target.getType());
        player.swingArm(hand);
        player.inventory.markDirty();
        return true;
    }
}
