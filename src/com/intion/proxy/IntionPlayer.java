package com.intion.proxy;

import com.intion.proxy.network.protocol.TransferPacket;

import java.net.InetSocketAddress;

public class IntionPlayer {

    private Session session;
    private String username;
    private String xuid;

    public IntionPlayer(Session session, String username, String xuid) {
        this.username = username;
        this.xuid = xuid;
        this.session = session;
    }

    public Session getSession() {
        return session;
    }

    public String getUsername() {
        return username;
    }

    public String getXuid() {
        return xuid;
    }

    public void transfer(InetSocketAddress address, String serverName)
    {
        TransferPacket packet = new TransferPacket();
        packet.playerName = this.username;
        packet.serverName = serverName;
        packet.address = address;
        this.session.dataPacket(packet);
    }
}
