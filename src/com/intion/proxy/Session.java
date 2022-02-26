package com.intion.proxy;

import com.intion.proxy.network.protocol.*;
import com.intion.proxy.utils.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session extends Thread {

    private Socket socket;
    private Loader loader;

    private long serverId;
    private String name;

    private int maxPlayers = 20;
    private int playerCount = 0;

    private Map<String, IntionPlayer> players = new HashMap<>();

    public Session(Loader loader, Socket socket)
    {
        this.loader = loader;
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
            Logger.log("Server connected with " + String.format("%s | %s | ID: %s", socket.getInetAddress().getHostAddress(), socket.getPort(), this.serverId));
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

                this.maxPlayers = connectionPacket.slots;
                this.name = connectionPacket.name;
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
                    values[i] = String.format("%s,%s,%s,%s",
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
                            this.players.put(playerDataPacket.xuid, new IntionPlayer(this, playerDataPacket.username, playerDataPacket.xuid));
                        Logger.log(String.format("%s connected to %s", playerDataPacket.username, this.serverId));
                        break;
                    case PlayerDataPacket.PlayerDataType.DISCONNECT:
                        this.players.remove(playerDataPacket.xuid);
                        Logger.log(String.format("%s disconnect from %s", playerDataPacket.username, this.serverId));
                        break;
                    case PlayerDataPacket.PlayerDataType.CHANGE_SERVER:
                        Logger.log(String.format("%s transferred from %s to %s", playerDataPacket.username, this.name,
                                playerDataPacket.serverName));
                        break;
                }
                break;
        }
    }

    private void disconnect()
    {
        this.disconnect("Unknown");
    }

    private void disconnect(String reason)
    {
        this.disconnect(reason, true);
    }

    private void disconnect(String reason, boolean send)
    {
        DisconnectPacket packet = new DisconnectPacket();
        packet.serverId = this.serverId;
        packet.reason = reason;
        Logger.log(String.format("ID: %s disconnect from %s", this.serverId, reason));
        if (send) {
            this.dataPacket(packet);
        }
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
        if (this.players.containsKey(uuid))
            return this.players.get(uuid);
        return null;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public String getSessionName() {
        return name;
    }
}
