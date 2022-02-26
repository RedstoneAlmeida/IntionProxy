package com.intion.proxy.network.protocol;

public class ConfirmationPacket extends DataPacket {

    public static byte NETWORK_ID = ProtocolInfo.CONFIRMATION_PACKET;

    public long serverId;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.serverId = this.getLong();
    }

    @Override
    public void encode() {
        this.putLong(this.serverId);
    }

}
