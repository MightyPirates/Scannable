/* SPDX-License-Identifier: MIT */

package li.cil.scannable.common.network.message;

import dev.architectury.networking.NetworkManager;
import li.cil.scannable.api.API;
import li.cil.scannable.common.container.AbstractModuleContainerMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public record SetConfiguredModuleItemAtMessage(int windowId, int index,
                                               Identifier value) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SetConfiguredModuleItemAtMessage> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(API.MOD_ID, "set_module_item"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SetConfiguredModuleItemAtMessage> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SetConfiguredModuleItemAtMessage::windowId,
            ByteBufCodecs.VAR_INT, SetConfiguredModuleItemAtMessage::index,
            Identifier.STREAM_CODEC, SetConfiguredModuleItemAtMessage::value,
            SetConfiguredModuleItemAtMessage::new);

    // --------------------------------------------------------------------- //

    public void handleMessage(final NetworkManager.PacketContext context) {
        if (context.getPlayer() instanceof ServerPlayer player &&
            player.containerMenu != null &&
            player.containerMenu.containerId == windowId &&
            player.containerMenu instanceof AbstractModuleContainerMenu menu) {
            menu.setItemAt(index, value);
        }
    }

    // --------------------------------------------------------------------- //
    // CustomPacketPayload

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
