package com.intion.proxy.network.protocol;

public class PingPacket extends DataPacket {

    public static byte NETWORK_ID = ProtocolInfo.PING_PACKET;

    public static int SEND_SESSION = 0;
    public static int SEND_PROXY = 1;

    public long timeStamp = System.currentTimeMillis();
    public long sessionStamp = System.currentTimeMillis();
    public int type = SEND_SESSION;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.timeStamp = this.getLong();
        this.sessionStamp = this.getLong();
        this.type = this.getInt();
    }

    @Override
    public void encode() {
        this.putLong(this.timeStamp);
        this.putLong(this.sessionStamp);
        this.putInt(this.type);
    }

    public static PingPacket create(long timeStamp, long sessionStamp, int type)
    {
        PingPacket packet = new PingPacket();
        packet.timeStamp = timeStamp;
        packet.sessionStamp = sessionStamp;
        packet.type = type;
        return packet;
    }
}
