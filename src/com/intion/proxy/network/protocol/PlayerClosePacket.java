package com.intion.proxy.network.protocol;

public class PlayerClosePacket extends DataPacket {

    public static byte NETWORK_ID = ProtocolInfo.PLAYER_CLOSE_PACKET;

    public String playerName;
    public String reason;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.playerName = this.getString();
        this.reason = this.getString();
    }

    @Override
    public void encode() {
        this.putString(this.playerName);
        this.putString(this.reason);
    }

    public static PlayerClosePacket create(String playerName, String reason)
    {
        PlayerClosePacket packet = new PlayerClosePacket();
        packet.playerName = playerName;
        packet.reason = reason;
        return packet;
    }
}
