package thederpgamer.betterfactions.network.client;

import api.common.GameCommon;
import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.betterfactions.data.faction.FactionData;
import thederpgamer.betterfactions.utils.FactionUtils;
import thederpgamer.betterfactions.utils.FederationUtils;
import java.io.IOException;

/**
 * CreateNewFederationPacket.java
 * <Description>
 * ==================================================
 * Created 02/08/2021
 * [Client -> Server]
 * @author TheDerpGamer
 */
public class CreateNewFederationPacket extends Packet {

    private String federationName;
    private int fromFactionId;
    private int toFactionId;

    public CreateNewFederationPacket() {

    }

    public CreateNewFederationPacket(String federationName, FactionData fromFaction, FactionData toFaction) {
        this.federationName = federationName;
        this.fromFactionId = fromFaction.getFactionId();
        this.toFactionId = toFaction.getFactionId();
    }

    @Override
    public void readPacketData(PacketReadBuffer packetReadBuffer) throws IOException {
        if(GameCommon.isDedicatedServer() || GameCommon.isOnSinglePlayer()) {
            federationName = packetReadBuffer.readString();
            fromFactionId = packetReadBuffer.readInt();
            toFactionId = packetReadBuffer.readInt();
        }
    }

    @Override
    public void writePacketData(PacketWriteBuffer packetWriteBuffer) throws IOException {
        if(GameCommon.isClientConnectedToServer() || GameCommon.isOnSinglePlayer()) {
            packetWriteBuffer.writeString(federationName);
            packetWriteBuffer.writeInt(fromFactionId);
            packetWriteBuffer.writeInt(toFactionId);
        }
    }

    @Override
    public void processPacketOnClient() {

    }

    @Override
    public void processPacketOnServer(PlayerState playerState) {
        FactionData fromFaction = FactionUtils.getFactionData(fromFactionId);
        FactionData toFaction = FactionUtils.getFactionData(toFactionId);
        FederationUtils.createNewFederation(federationName, fromFaction, toFaction);
    }
}
