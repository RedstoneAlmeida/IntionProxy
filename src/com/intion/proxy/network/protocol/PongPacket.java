package com.intion.proxy.network.protocol;

public class PongPacket extends DataPacket {

    public static byte NETWORK_ID = ProtocolInfo.PONG_PACKET;

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

    public static PongPacket create()
    {
        return new PongPacket();
    }
}
