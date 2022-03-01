package com.intion.proxy.plugin;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerLoginEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.plugin.PluginBase;
import com.intion.proxy.network.Network;
import com.intion.proxy.network.protocol.PlayerDataPacket;

import java.util.Random;

public class Loader extends PluginBase implements Listener {

    private Network network;
    private Session session;

    @Override
    public void onEnable() {
        this.network = new Network();
        this.getDataFolder().mkdir();

        this.saveDefaultConfig();

        String address = this.getConfig().getString("address", "0.0.0.0");
        int port = this.getConfig().getInt("port", 25567);
        String password = this.getConfig().getString("password", "Intion@123");
        this.getLogger().info("§6Session Starting...");
        this.session = new Session(this, address, port, password);
        this.session.start();

        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event)
    {
        Player player = event.getPlayer();
        PlayerDataPacket packet = new PlayerDataPacket();
        packet.type = PlayerDataPacket.PlayerDataType.CONNECT;
        packet.xuid = player.getLoginChainData().getXUID();
        packet.username = player.getName();
        packet.serverName = this.getConfig().getString("name", "Unknown");
        this.session.sendPacket(packet);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        PlayerDataPacket packet = new PlayerDataPacket();
        packet.type = PlayerDataPacket.PlayerDataType.DISCONNECT;
        packet.xuid = player.getLoginChainData().getXUID();
        packet.username = player.getName();
        packet.serverName = this.getConfig().getString("name", "Unknown");
        this.session.sendPacket(packet);
    }

    public Session getSession() {
        return session;
    }

    public Network getNetwork() {
        return network;
    }
}
