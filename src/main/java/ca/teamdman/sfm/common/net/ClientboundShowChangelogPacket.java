package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.client.gui.screen.SFMScreenHelpers;
import net.minecraft.network.FriendlyByteBuf;

public record ClientboundShowChangelogPacket(
) implements SFMPacket {

    public static class Daddy implements SFMPacketDaddy<ClientboundShowChangelogPacket> {
        @Override
        public PacketDirection getPacketDirection() {
            return PacketDirection.CLIENTBOUND;
        }
        @Override
        public void encode(
                ClientboundShowChangelogPacket msg,
                FriendlyByteBuf friendlyByteBuf
        ) {
        }

        @Override
        public ClientboundShowChangelogPacket decode(FriendlyByteBuf friendlyByteBuf) {
            return new ClientboundShowChangelogPacket(
            );
        }

        @Override
        public void handle(
                ClientboundShowChangelogPacket msg,
                SFMPacketHandlingContext context
        ) {
            SFMScreenHelpers.showChangelog();
        }

        @Override
        public Class<ClientboundShowChangelogPacket> getPacketClass() {
            return ClientboundShowChangelogPacket.class;
        }
    }
}
