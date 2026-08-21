/* SPDX-License-Identifier: MIT */

package li.cil.scannable.common.network.message;

import dev.architectury.networking.NetworkManager;
import li.cil.scannable.api.API;
import li.cil.scannable.common.container.AbstractModuleContainerMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public record RemoveConfiguredModuleItemAtMessage(int windowId, int index) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<RemoveConfiguredModuleItemAtMessage> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(API.MOD_ID, "remove_module_item"));

    public static final StreamCodec<RegistryFriendlyByteBuf, RemoveConfiguredModuleItemAtMessage> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.VAR_INT, RemoveConfiguredModuleItemAtMessage::windowId,
            ByteBufCodecs.VAR_INT, RemoveConfiguredModuleItemAtMessage::index,
            RemoveConfiguredModuleItemAtMessage::new);

    // --------------------------------------------------------------------- //

    public void handleMessage(final NetworkManager.PacketContext context) {
        if (context.getPlayer() instanceof ServerPlayer player &&
            player.containerMenu != null &&
            player.containerMenu.containerId == windowId &&
            player.containerMenu instanceof AbstractModuleContainerMenu menu) {
            menu.removeItemAt(index);
        }
    }

    // --------------------------------------------------------------------- //
    // CustomPacketPayload

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
