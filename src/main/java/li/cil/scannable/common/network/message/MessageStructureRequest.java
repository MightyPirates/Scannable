package li.cil.scannable.common.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public final class MessageStructureRequest implements IMessage {
    private int dimension;
    private BlockPos center;
    private float radius;
    private boolean hideExplored;

    // --------------------------------------------------------------------- //

    public MessageStructureRequest(final World world, final BlockPos center, final float radius, final boolean hideExplored) {
        this.dimension = world.provider.getDimension();
        this.center = center;
        this.radius = radius;
        this.hideExplored = hideExplored;
    }

    @SuppressWarnings("unused") // For deserialization.
    public MessageStructureRequest() {
    }

    public int getDimension() {
        return dimension;
    }

    public BlockPos getCenter() {
        return center;
    }

    public float getRadius() {
        return radius;
    }

    public boolean hideExplored() {
        return hideExplored;
    }

    // --------------------------------------------------------------------- //
    // IMessage

    @Override
    public void fromBytes(final ByteBuf buf) {
        final PacketBuffer packet = new PacketBuffer(buf);
        dimension = packet.readInt();
        center = packet.readBlockPos();
        radius = packet.readFloat();
        hideExplored = packet.readBoolean();
    }

    @Override
    public void toBytes(final ByteBuf buf) {
        final PacketBuffer packet = new PacketBuffer(buf);
        packet.writeInt(dimension);
        packet.writeBlockPos(center);
        packet.writeFloat(radius);
        packet.writeBoolean(hideExplored);
    }
}
