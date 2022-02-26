package com.intion.proxy.network.protocol;

import com.intion.proxy.IntionPlayer;

import java.net.InetSocketAddress;

public class TransferPacket extends DataPacket {

    public static byte NETWORK_ID = ProtocolInfo.TRANSFER_PACKET;

    public String playerName;
    public InetSocketAddress address;
    public String serverName = "Unknown";

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.playerName = this.getString();
        this.address = new InetSocketAddress(this.getString(), this.getInt());
        this.serverName = this.getString();
    }

    @Override
    public void encode() {
        this.putString(this.playerName);
        this.putString(this.address.getHostName());
        this.putInt(this.address.getPort());
        this.putString(this.serverName);
    }
}
