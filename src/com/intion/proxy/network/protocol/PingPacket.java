package com.intion.proxy.network.protocol;

public class PingPacket extends DataPacket {

    public static byte NETWORK_ID = ProtocolInfo.PING_PACKET;

    public long timeStamp = System.currentTimeMillis();

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.timeStamp = this.getLong();
    }

    @Override
    public void encode() {
        this.putLong(this.timeStamp);
    }

    public static PingPacket create(long timeStamp)
    {
        PingPacket packet = new PingPacket();
        packet.timeStamp = timeStamp;
        return packet;
    }
}
