package li.cil.scannable.common.network.message;

import li.cil.scannable.client.scanning.ScanResultProviderStructure;
import li.cil.scannable.client.scanning.ScanResultProviders;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public final class StructureResponseMessage extends AbstractMessage {
    private ScanResultProviderStructure.StructureLocation[] structures;

    // --------------------------------------------------------------------- //

    public StructureResponseMessage(final ScanResultProviderStructure.StructureLocation[] structures) {
        this.structures = structures;
    }

    public StructureResponseMessage(final FriendlyByteBuf buffer) {
        super(buffer);
    }

    // --------------------------------------------------------------------- //

    @Override
    protected void handleMessage(final NetworkEvent.Context context) {
        ScanResultProviders.STRUCTURES.get().setStructures(structures);
    }

    @Override
    public void fromBytes(final FriendlyByteBuf buffer) {
        final int length = buffer.readInt();
        structures = new ScanResultProviderStructure.StructureLocation[length];
        for (int i = 0; i < length; i++) {
            final Component name = buffer.readComponent();
            final BlockPos pos = buffer.readBlockPos();
            structures[i] = new ScanResultProviderStructure.StructureLocation(name, pos);
        }
    }

    @Override
    public void toBytes(final FriendlyByteBuf buffer) {
        buffer.writeInt(structures.length);
        for (final ScanResultProviderStructure.StructureLocation structure : structures) {
            buffer.writeComponent(structure.name());
            buffer.writeBlockPos(structure.pos());
        }
    }
}
