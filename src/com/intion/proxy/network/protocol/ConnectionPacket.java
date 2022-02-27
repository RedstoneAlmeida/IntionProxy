package com.intion.proxy.network.protocol;

import java.net.InetSocketAddress;

/**
 * Created by ASUS on 19/02/2018.
 */
public class ConnectionPacket extends DataPacket {

    public static byte NETWORK_ID = ProtocolInfo.CONNECTION_PACKET;

    public String name;
    public String password;
    public int slots = 0;

    public InetSocketAddress address;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.name = this.getString();
        this.password = this.getString();
        this.slots = this.getInt();
        String hostname = this.getString();
        int port = this.getInt();
        this.address = new InetSocketAddress(hostname, port);
    }

    @Override
    public void encode() {
        this.putString(name);
        this.putString(password);
        this.putInt(slots);
        this.putString(address.getHostName());
        this.putInt(address.getPort());
    }

}
