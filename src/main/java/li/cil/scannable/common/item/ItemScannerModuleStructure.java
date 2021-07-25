package li.cil.scannable.common.item;

import li.cil.scannable.common.config.Constants;
import li.cil.scannable.common.scanning.ScannerModuleStructure;
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

public final class ItemScannerModuleStructure extends AbstractItemScannerModule {
    private static final String TAG_HIDE_EXPLORED = "hideExplored";

    public static boolean shouldHideExplored(final ItemStack stack) {
        final CompoundTag nbt = stack.getTag();
        return nbt != null && nbt.getBoolean(TAG_HIDE_EXPLORED);
    }

    public static void setHideExplored(final ItemStack stack, final boolean hideExplored) {
        final CompoundTag nbt = stack.getOrCreateTag();
        nbt.putBoolean(TAG_HIDE_EXPLORED, hideExplored);
    }

    // --------------------------------------------------------------------- //

    public ItemScannerModuleStructure() {
        super(ScannerModuleStructure.INSTANCE);
    }

    // --------------------------------------------------------------------- //
    // Item

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(final ItemStack stack, @Nullable final Level world, final List<Component> tooltip, final TooltipFlag flag) {
        tooltip.add(new TranslatableComponent(Constants.TOOLTIP_MODULE_STRUCTURE));
        if (shouldHideExplored(stack)) {
            tooltip.add(new TranslatableComponent(Constants.TOOLTIP_MODULE_STRUCTURE_HIDE_EXPLORED));
        } else {
            tooltip.add(new TranslatableComponent(Constants.TOOLTIP_MODULE_STRUCTURE_SHOW_EXPLORED));
        }
        super.appendHoverText(stack, world, tooltip, flag);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(final Level world, final Player player, final InteractionHand hand) {
        final ItemStack stack = player.getItemInHand(hand);

        if (!player.isShiftKeyDown()) {
            return InteractionResultHolder.pass(stack);
        }

        setHideExplored(stack, !shouldHideExplored(stack));

        return InteractionResultHolder.success(stack);
    }
}
