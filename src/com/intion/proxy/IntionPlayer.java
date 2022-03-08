package com.intion.proxy;

import com.intion.proxy.event.session.player.PlayerKickEvent;
import com.intion.proxy.network.protocol.PlayerClosePacket;
import com.intion.proxy.network.protocol.PlayerMessagePacket;
import com.intion.proxy.network.protocol.TransferPacket;
import com.intion.proxy.utils.IntionConfig;
import com.intion.proxy.utils.Logger;
import com.intion.proxy.utils.LoggerEnum;

import java.net.InetSocketAddress;

public class IntionPlayer {

    private Session session;
    private String username;
    private String xuid;
    private IntionConfig config;

    public IntionPlayer(Session session, String username, String xuid) {
        this.username = username;
        this.xuid = xuid;
        this.session = session;
        this.config = session.getLoader().getBanConfig();
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

    public void close()
    {
        this.close("Kicked by Proxy Admin");
    }

    public void close(String reason)
    {
        int type = PlayerKickEvent.OTHER;
        if (this.isBanned())
            type = PlayerKickEvent.BANNED;
        PlayerKickEvent event = new PlayerKickEvent(this.session, this, type, reason);
        this.session.getLoader().getPluginLoader().getManager().callEvent(event);

        LoggerEnum.INFO.log(String.format("[%s] %s disconnected, reason %s",
                this.session.getSessionName(),
                this.username, event.getReason()));
        this.session.dataPacket(PlayerClosePacket.create(this.username, reason));
    }

    public boolean isBanned()
    {
        if (this.config.exist(this.getUsername()))
            return true;
        if (this.config.exist(this.getXuid()))
            return true;
        return false;
    }

    public void sendMessage(String message)
    {
        this.session.dataPacket(PlayerMessagePacket.create(this.username, message));
    }

    public void sendTitle(String message)
    {
        this.session.dataPacket(PlayerMessagePacket.create(this.username, message, PlayerMessagePacket.TYPE_TITLE));
    }

    public void sendTip(String message)
    {
        this.session.dataPacket(PlayerMessagePacket.create(this.username, message, PlayerMessagePacket.TYPE_TIP));
    }

    public void sendActionBar(String message)
    {
        this.session.dataPacket(PlayerMessagePacket.create(this.username, message, PlayerMessagePacket.TYPE_ACTION_BAR));
    }
}
