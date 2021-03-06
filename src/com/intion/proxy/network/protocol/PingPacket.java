package com.intion.proxy.network.protocol;

public class PingPacket extends DataPacket {

    public static byte NETWORK_ID = ProtocolInfo.PING_PACKET;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {

    }

    @Override
    public void encode() {

    }

    public static PingPacket create()
    {
        return new PingPacket();
    }
}
