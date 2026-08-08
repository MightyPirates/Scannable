package li.cil.scannable.common.inventory;

import li.cil.scannable.common.item.Items;
import li.cil.scannable.common.item.ModDataComponents;
import li.cil.scannable.common.item.ScannerItem;
import li.cil.scannable.common.item.ScannerModuleItem;
import net.minecraft.core.NonNullList;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

import java.util.ArrayList;
import java.util.List;

public final class ScannerContainer extends SimpleContainer {
    private static final int ACTIVE_MODULE_COUNT = 3;
    private static final int INACTIVE_MODULE_COUNT = 6;
    private static final int TOTAL_MODULE_COUNT = ACTIVE_MODULE_COUNT + INACTIVE_MODULE_COUNT;

    private final ItemStack container;

    public ScannerContainer(final ItemStack container) {
        super(TOTAL_MODULE_COUNT);
        this.container = container;

        final ItemContainerContents contents = container.get(ModDataComponents.MODULES.get());
        if (contents != null) {
            final NonNullList<ItemStack> items = NonNullList.withSize(TOTAL_MODULE_COUNT, ItemStack.EMPTY);
            contents.copyInto(items);
            for (int slot = 0; slot < TOTAL_MODULE_COUNT; slot++) {
                setItem(slot, items.get(slot));
            }
        }
    }

    public static ScannerContainer of(final ItemStack container) {
        if (container.getItem() instanceof ScannerItem) {
            return new ScannerContainer(container);
        } else {
            return new ScannerContainer(new ItemStack(Items.SCANNER.get()));
        }
    }

    public ContainerSlice getActiveModules() {
        return new ContainerSlice(this, 0, ACTIVE_MODULE_COUNT);
    }

    public ContainerSlice getInactiveModules() {
        return new ContainerSlice(this, ACTIVE_MODULE_COUNT, INACTIVE_MODULE_COUNT);
    }

    // --------------------------------------------------------------------- //
    // Container

    @Override
    public void setItem(final int i, final ItemStack itemStack) {
        if (canPlaceItem(i, itemStack)) {
            super.setItem(i, itemStack);
        }
    }

    @Override
    public boolean canPlaceItem(final int i, final ItemStack stack) {
        return isModule(stack) && super.canPlaceItem(i, stack);
    }

    @Override
    public void setChanged() {
        super.setChanged();

        final List<ItemStack> items = new ArrayList<>(getContainerSize());
        for (int slot = 0; slot < getContainerSize(); slot++) {
            items.add(getItem(slot));
        }
        container.set(ModDataComponents.MODULES.get(), ItemContainerContents.fromItems(items));
    }

    // --------------------------------------------------------------------- //
    // SimpleContainer

    @Override
    public ItemStack addItem(final ItemStack stack) {
        if (canAddItem(stack)) {
            return super.addItem(stack);
        } else {
            return stack;
        }
    }

    @Override
    public boolean canAddItem(final ItemStack stack) {
        return isModule(stack) && super.canAddItem(stack);
    }

    // --------------------------------------------------------------------- //

    private boolean isModule(final ItemStack stack) {
        // All built-in modules, including those without capability such as the range module.
        if (stack.getItem() instanceof ScannerModuleItem) {
            return true;
        }

        // External modules declared via capability/interface.
        if (ScannerModuleItem.getModule(stack).isPresent()) {
            return true;
        }

        return false;
    }
}
