package li.cil.scannable.common.container;

import li.cil.scannable.common.Scannable;
import li.cil.scannable.common.item.ItemScannerModuleEntityConfigurable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;

import java.util.Optional;

public class EntityModuleContainer extends AbstractModuleContainer {
    public static EntityModuleContainer createForServer(final int windowId, final PlayerInventory inventory, final Hand hand) {
        return new EntityModuleContainer(windowId, inventory, hand);
    }

    public static EntityModuleContainer createForClient(final int windowId, final PlayerInventory inventory, final PacketBuffer buffer) {
        final Hand hand = buffer.readEnumValue(Hand.class);
        return new EntityModuleContainer(windowId, inventory, hand);
    }

    // --------------------------------------------------------------------- //

    public EntityModuleContainer(final int windowId, final PlayerInventory inventory, final Hand hand) {
        super(Scannable.ENTITY_MODULE_CONTAINER.get(), windowId, inventory, hand);
    }

    @Override
    public void removeItemAt(final int index) {
        final ItemStack stack = getPlayer().getHeldItem(getHand());
        ItemScannerModuleEntityConfigurable.removeEntityTypeAt(stack, index);
    }

    @Override
    public void setItemAt(final int index, final String value) {
        final ItemStack stack = getPlayer().getHeldItem(getHand());
        final Optional<EntityType<?>> entityType = EntityType.byKey(value);
        entityType.ifPresent(e -> {
            ItemScannerModuleEntityConfigurable.setEntityTypeAt(stack, index, e);
        });
    }
}
