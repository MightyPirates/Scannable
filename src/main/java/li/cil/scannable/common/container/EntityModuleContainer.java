package li.cil.scannable.common.container;

import li.cil.scannable.common.item.ItemScannerModuleEntityConfigurable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class EntityModuleContainer extends AbstractModuleContainer {
    public static EntityModuleContainer create(final int windowId, final Inventory inventory, final FriendlyByteBuf buffer) {
        final InteractionHand hand = buffer.readEnum(InteractionHand.class);
        return new EntityModuleContainer(windowId, inventory, hand);
    }

    // --------------------------------------------------------------------- //

    public EntityModuleContainer(final int windowId, final Inventory inventory, final InteractionHand hand) {
        super(Containers.ENTITY_MODULE_CONTAINER.get(), windowId, inventory, hand);
    }

    @Override
    public void removeItemAt(final int index) {
        final ItemStack stack = getPlayer().getItemInHand(getHand());
        ItemScannerModuleEntityConfigurable.removeEntityTypeAt(stack, index);
    }

    @Override
    public void setItemAt(final int index, final ResourceLocation name) {
        final ItemStack stack = getPlayer().getItemInHand(getHand());
        final EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(name);
        if (entityType != null) {
            ItemScannerModuleEntityConfigurable.setEntityTypeAt(stack, index, entityType);
        }
    }
}
