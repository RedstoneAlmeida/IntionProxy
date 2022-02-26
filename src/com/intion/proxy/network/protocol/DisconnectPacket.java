package com.intion.proxy.network.protocol;

/**
 * Created by ASUS on 19/02/2018.(asus old)
 */
public class DisconnectPacket extends DataPacket {

    public static byte NETWORK_ID = ProtocolInfo.DISCONNECTION_PACKET;

    public long serverId;
    public String reason;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.serverId = this.getLong();
        this.reason = this.getString();
    }

    @Override
    public void encode() {
        this.putLong(serverId);
        this.putString(reason);
    }
}