package li.cil.scannable.client.scanning.filter;

import li.cil.scannable.api.scanning.ScanFilterBlock;
import net.minecraft.block.BlockState;

import java.util.List;

public final class ScanFilterBlockList implements ScanFilterBlock {
    private final List<ScanFilterBlock> filters;

    public ScanFilterBlockList(final List<ScanFilterBlock> filters) {
        this.filters = filters;
    }

    @Override
    public boolean matches(final BlockState state) {
        return filters.stream().anyMatch(f -> f.matches(state));
    }
}
