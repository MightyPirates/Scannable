package li.cil.scannable.common.item;

import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.scanning.StructureScannerModule;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public final class StructureScannerModuleItem extends AbstractScannerModuleItem {
    private static final String TAG_HIDE_EXPLORED = "hideExplored";

    public static boolean shouldHideExplored(final ItemStack stack) {
        final CompoundTag tag = stack.getTag();
        return tag != null && tag.getBoolean(TAG_HIDE_EXPLORED);
    }

    public static void setHideExplored(final ItemStack stack, final boolean hideExplored) {
        final CompoundTag tag = stack.getOrCreateTag();
        tag.putBoolean(TAG_HIDE_EXPLORED, hideExplored);
    }

    // --------------------------------------------------------------------- //

    public StructureScannerModuleItem() {
        super(StructureScannerModule.INSTANCE);
    }

    // --------------------------------------------------------------------- //
    // Item

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(final ItemStack stack, @Nullable final Level level, final List<Component> tooltip, final TooltipFlag flag) {
        tooltip.add(new TranslatableComponent(Constants.TOOLTIP_MODULE_STRUCTURE));
        if (shouldHideExplored(stack)) {
            tooltip.add(new TranslatableComponent(Constants.TOOLTIP_MODULE_STRUCTURE_HIDE_EXPLORED));
        } else {
            tooltip.add(new TranslatableComponent(Constants.TOOLTIP_MODULE_STRUCTURE_SHOW_EXPLORED));
        }
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(final Level level, final Player player, final InteractionHand hand) {
        final ItemStack stack = player.getItemInHand(hand);
        if (!player.isShiftKeyDown()) {
            return InteractionResultHolder.pass(stack);
        }

        setHideExplored(stack, !shouldHideExplored(stack));

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
