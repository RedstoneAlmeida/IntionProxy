package com.intion.proxy;

import com.intion.proxy.event.session.SessionDataPacketReceiveEvent;
import com.intion.proxy.event.session.SessionDataPacketSendEvent;
import com.intion.proxy.event.session.SessionDisconnectEvent;
import com.intion.proxy.event.session.SessionInitializeEvent;
import com.intion.proxy.event.session.player.PlayerConnectEvent;
import com.intion.proxy.event.session.player.PlayerDisconnectEvent;
import com.intion.proxy.event.session.player.PlayerTransferEvent;
import com.intion.proxy.microplugin.PluginLoader;
import com.intion.proxy.network.protocol.*;
import com.intion.proxy.utils.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session extends Thread {

    private Socket socket;
    private Loader loader;
    private PluginLoader pluginLoader;

    private long serverId;
    private String name;
    private InetSocketAddress address;

    private int maxPlayers = 20;
    private int playerCount = 0;

    private boolean initialized = false;

    private Map<String, IntionPlayer> players = new HashMap<>();

    public Session(Loader loader, Socket socket)
    {
        this.loader = loader;
        this.pluginLoader = loader.getPluginLoader();
        this.socket = socket;
    }

    public long getServerId() {
        return serverId;
    }

    public void setServerId(long serverId) {
        this.serverId = serverId;
    }

    public Loader getLoader() {
        return loader;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {
        try {
            Logger.log("Server connected with " + String.format("%s:%s | ID: %s", socket.getInetAddress().getHostAddress(), socket.getPort(), this.serverId));
            while (true)
            {
                if (!socket.isConnected()) return;
                DataInputStream input = new DataInputStream(socket.getInputStream());
                byte id = input.readByte();
                int length = input.readInt();
                byte[] buffer = new byte[length];
                input.readFully(buffer);

                DataPacket packet = this.loader.getNetwork().getPacket(id);
                packet.setBuffer(buffer);
                packet.decode();

                this.handlePacket(packet);
            }
        } catch (IOException e)
        {
            if (e.getMessage() == null)
                return;
            if (e.getMessage().contains("Connection reset")) {
                try {
                    this.disconnect("other causes", false);
                    socket.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                return;
            }

            e.printStackTrace();
        }
    }

    public void dataPacket(DataPacket packet)
    {
        SessionDataPacketSendEvent event = new SessionDataPacketSendEvent(this, packet);
        this.pluginLoader.getManager().callEvent(event);

        if (event.isCancelled())
            return;

        packet.encode();
        try {
            DataOutputStream saida = new DataOutputStream(this.socket.getOutputStream());
            saida.writeByte(packet.pid());
            byte[] buffered = packet.getBuffer();
            saida.writeInt(buffered.length);
            saida.write(buffered);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handlePacket(DataPacket packet)
    {
        SessionDataPacketReceiveEvent event = new SessionDataPacketReceiveEvent(this, packet);
        this.pluginLoader.getManager().callEvent(event);

        if (event.isCancelled())
            return;

        switch (packet.pid())
        {
            case ProtocolInfo.HANDLER_PACKET:
                break;
            case ProtocolInfo.CONNECTION_PACKET:
                ConnectionPacket connectionPacket = (ConnectionPacket) packet;
                if (!connectionPacket.password.equals(this.loader.getToken()))
                {
                    this.disconnect("Password incorrect");
                    return;
                }
                // check password is correctly :D
                ConfirmationPacket confirmationPacket = new ConfirmationPacket();
                confirmationPacket.serverId = this.serverId;
                this.dataPacket(confirmationPacket);

                InetAddress address = this.socket.getInetAddress();
                InetSocketAddress inetSocketAddress = new InetSocketAddress(address.getHostName(), this.socket.getPort());
                SessionInitializeEvent sessionInitializeEvent = new SessionInitializeEvent(this, connectionPacket.slots, connectionPacket.name, inetSocketAddress);
                this.pluginLoader.getManager().callEvent(sessionInitializeEvent);

                this.maxPlayers = sessionInitializeEvent.getSlots();
                this.name = sessionInitializeEvent.getServerName();
                this.address = connectionPacket.address;

                this.initialized = true;

                Logger.log("ID: " + this.serverId + " -> fully connected, server name: " + this.name);
                break;
            case ProtocolInfo.INFORMATION_PACKET:
                InformationPacket informationPacket = (InformationPacket) packet;
                Logger.log(informationPacket.information);
                break;
            case ProtocolInfo.SERVER_INFORMATION_PACKET:
                ServerInformationPacket serverInformationPacket = (ServerInformationPacket) packet;
                if (this.serverId == serverInformationPacket.serverId)
                {
                    this.playerCount = serverInformationPacket.onlinePlayers;
                }
                break;
            case ProtocolInfo.LIST_PACKET:
                ListPacket listPacket = new ListPacket();
                Map<Long, Session> sessions = loader.getSessions();
                String[] values = new String[sessions.size()];
                int i = 0;
                for (long id : sessions.keySet())
                {
                    Session session = sessions.get(id);
                    values[i] = String.format("%s (%s),%s,%s,%s",
                            session.getSessionName(),
                            session.getServerId(),
                            session.getSessionName(),
                            session.getPlayerCount(),
                            session.getMaxPlayers());
                    i++;
                }
                listPacket.list = values;
                this.dataPacket(listPacket);
                break;
            case ProtocolInfo.PLAYER_DATA_PACKET:
                PlayerDataPacket playerDataPacket = (PlayerDataPacket) packet;
                switch (playerDataPacket.type)
                {
                    case PlayerDataPacket.PlayerDataType.CONNECT:
                        if (!this.players.containsKey(playerDataPacket.xuid))
                        {
                            IntionPlayer player = new IntionPlayer(this, playerDataPacket.username, playerDataPacket.xuid);
                            PlayerConnectEvent playerConnectEvent = new PlayerConnectEvent(this, player);
                            this.pluginLoader.getManager().callEvent(playerConnectEvent);
                            this.players.put(playerDataPacket.xuid, player);
                        }
                        if (this.initialized)
                            Logger.log(String.format("%s connected to %s (%s)", playerDataPacket.username, this.serverId, this.getSessionName()));
                        else
                            Logger.log(String.format("%s connected to %s (Unknown)", playerDataPacket.username, this.serverId));
                        break;
                    case PlayerDataPacket.PlayerDataType.DISCONNECT:
                        if (this.players.containsKey(playerDataPacket.xuid))
                        {
                            this.players.remove(playerDataPacket.xuid);
                            IntionPlayer player = this.players.get(playerDataPacket.xuid);

                            PlayerDisconnectEvent playerDisconnectEvent = new PlayerDisconnectEvent(this, player);
                            this.pluginLoader.getManager().callEvent(playerDisconnectEvent);
                        }
                        Logger.log(String.format("%s disconnect from %s", playerDataPacket.username, this.serverId));
                        break;
                    case PlayerDataPacket.PlayerDataType.CHANGE_SERVER:
                        if (this.players.containsKey(playerDataPacket.xuid))
                        {
                            this.players.remove(playerDataPacket.xuid);
                            IntionPlayer player = this.players.get(playerDataPacket.xuid);

                            PlayerTransferEvent playerTransferEvent = new PlayerTransferEvent(this, player);
                            this.pluginLoader.getManager().callEvent(playerTransferEvent);
                        }
                        Logger.log(String.format("%s transferred from %s to %s", playerDataPacket.username, this.name,
                                playerDataPacket.serverName));
                        break;
                }
                break;
        }
    }

    public void disconnect()
    {
        this.disconnect("Unknown");
    }

    public void disconnect(String reason)
    {
        this.disconnect(reason, true);
    }

    public void disconnect(String reason, boolean send)
    {
        DisconnectPacket packet = new DisconnectPacket();
        packet.serverId = this.serverId;
        packet.reason = reason;
        Logger.log(String.format("ID: %s disconnect from %s", this.serverId, reason));
        if (send) {
            this.dataPacket(packet);
        }

        SessionDisconnectEvent disconnectEvent = new SessionDisconnectEvent(this, reason);
        this.pluginLoader.getManager().callEvent(disconnectEvent);

        this.loader.removeSession(this.serverId);
    }

    public Map<String, IntionPlayer> getPlayers() {
        return players;
    }

    public IntionPlayer getPlayerByName(String name)
    {
        for (IntionPlayer player : this.players.values())
        {
            if (player.getUsername().startsWith(name))
                return player;
        }
        return null;
    }

    public IntionPlayer getPlayerByUUID(UUID uuid)
    {
        return this.getPlayerByUUID(uuid.toString());
    }

    public IntionPlayer getPlayerByUUID(String uuid)
    {
        return this.players.getOrDefault(uuid, null);
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public String getSessionName() {
        if (name == null)
            return "Unknown";
        return name;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public InetSocketAddress getServerAddress() {
        if (address.getHostName().equalsIgnoreCase("0.0.0.0"))
            return new InetSocketAddress(this.socket.getInetAddress().getHostAddress(), address.getPort());
        return address;
    }

    @Override
    public String toString() {
        return "Session{" +
                "serverId=" + serverId +
                ", name='" + name + '\'' +
                ", maxPlayers=" + maxPlayers +
                ", playerCount=" + playerCount +
                ", initialized=" + initialized +
                '}';
    }
}
