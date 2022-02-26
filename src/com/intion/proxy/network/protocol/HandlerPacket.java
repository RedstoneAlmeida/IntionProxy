package com.intion.proxy.network.protocol;

public class HandlerPacket extends DataPacket{

    public static byte NETWORK_ID = ProtocolInfo.HANDLER_PACKET;

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

}
