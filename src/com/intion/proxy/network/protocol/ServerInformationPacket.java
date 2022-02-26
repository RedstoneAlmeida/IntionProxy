package com.intion.proxy.network.protocol;

/**
 * Created by ASUS on 21/02/2018.
 */
public class ServerInformationPacket extends DataPacket {

    public static byte NETWORK_ID = ProtocolInfo.SERVER_INFORMATION_PACKET;

    public int onlinePlayers;
    public long serverId;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.onlinePlayers = this.getInt();
        this.serverId = this.getLong();
    }

    @Override
    public void encode() {
        this.putInt(onlinePlayers);
        this.putLong(serverId);
    }
}
