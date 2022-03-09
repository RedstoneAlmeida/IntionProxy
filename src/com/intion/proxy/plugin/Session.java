package com.intion.proxy.plugin;

import cn.nukkit.Player;
import cn.nukkit.command.ConsoleCommandSender;
import com.intion.proxy.network.protocol.*;
import com.intion.proxy.utils.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Session extends Thread {

    private Socket socket = null;
    private Loader loader;
    private long serverId = -1;

    private String address, password;
    private int port;

    private int tries = 0;

    private ConcurrentLinkedQueue<DataPacket> queue = new ConcurrentLinkedQueue<>();

    public Session(Loader loader, String address, int port, String password)
    {
        this.loader = loader;
        this.address = address;
        this.port = port;
        this.password = password;
    }

    @Override
    public void run()
    {
        try {
            this.socket = new Socket(this.address, this.port);
            this.loader.getLogger().info("§6Connected with proxy");

            ConnectionPacket pk = ConnectionPacket.create(
                    this.loader.getConfig().getString("name", "Unknown"),
                    this.password,
                    this.loader.getServer().getMaxPlayers(),
                    new InetSocketAddress(this.loader.getServer().getIp(), this.loader.getServer().getPort())
            );

            this.dataPacket(pk);

            // send first packet

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

                if (!this.queue.isEmpty())
                {
                    DataPacket dataPacket = this.queue.poll();
                    this.dataPacket(dataPacket);
                }
            }
        } catch (IOException e) {
            if (e.getMessage().contains("Socket is closed"))
            {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                return;
            }
            if (e.getMessage().contains("refused"))
            {
                if (this.tries > 2){
                    this.loader.getLogger().info("§6Proxy is offline, disconnected");
                    this.tries = 0;
                    return;
                }
                this.loader.getLogger().info("§6Proxy not connected, retrying");
                this.tries++;
                this.run();
                return;
            }
            if (e.getMessage().contains("Connection reset")) {
                try {
                    this.loader.getLogger().info("§6Proxy disconnected");
                    socket.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                return;
            }
            e.printStackTrace();
        }
    }

    public void dataPacket(DataPacket packet) throws IOException
    {
        if (this.socket == null)
            return;
        packet.encode();
        DataOutputStream saida = new DataOutputStream(this.socket.getOutputStream());
        saida.writeByte(packet.pid());
        byte[] buffered = packet.getBuffer();
        saida.writeInt(buffered.length);
        saida.write(buffered);
    }

    public void sendPacket(DataPacket packet)
    {
        this.sendPacket(packet, false);
    }

    public void sendPacket(DataPacket packet, boolean async)
    {
        if (async)
        {
            this.queue.add(packet);
            return;
        }
        try {
            this.dataPacket(packet);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void handlePacket(DataPacket packet)
    {
        switch (packet.pid())
        {
            case ProtocolInfo.HANDLER_PACKET:
                this.sendPacket((HandlerPacket) packet);
                break;
            case ProtocolInfo.CONFIRMATION_PACKET:
                ConfirmationPacket confirmationPacket = (ConfirmationPacket) packet;
                this.serverId = confirmationPacket.serverId;
                //this.loader.getLogger().info(String.valueOf(this.serverId));
                break;
            case ProtocolInfo.DISCONNECTION_PACKET:
                try {
                    this.loader.getLogger().info("§6Disconnected from proxy: " + ((DisconnectPacket) packet).reason);
                    this.socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case ProtocolInfo.COMMAND_PACKET:
                CommandPacket commandPacket = (CommandPacket) packet;
                this.loader.getServer().getCommandMap().dispatch(this.loader.getServer().getConsoleSender(), commandPacket.command);
                break;
            case ProtocolInfo.INFORMATION_PACKET:
                InformationPacket informationPacket = (InformationPacket) packet;
                if (informationPacket.information.equals("playerCount"))
                {
                    ServerInformationPacket serverInformationPacket = new ServerInformationPacket();
                    serverInformationPacket.onlinePlayers = this.loader.getServer().getOnlinePlayers().size();
                    serverInformationPacket.serverId = this.serverId;
                    this.sendPacket(serverInformationPacket);
                    return;
                }
                break;
            case ProtocolInfo.LIST_PACKET:
                ListPacket listPacket = (ListPacket) packet;
                for(String s : listPacket.list)
                {
                    this.loader.getLogger().info(s);
                }
                break;
            case ProtocolInfo.TRANSFER_PACKET:
                TransferPacket transferPacket = (TransferPacket) packet;
                Player player = this.loader.getServer().getPlayer(transferPacket.playerName);
                if (player == null)
                    return;
                PlayerDataPacket playerDataPacket = new PlayerDataPacket();
                playerDataPacket.serverName = transferPacket.serverName;
                playerDataPacket.username = player.getName();
                playerDataPacket.xuid = player.getUniqueId().toString();
                playerDataPacket.type = PlayerDataPacket.PlayerDataType.CHANGE_SERVER;
                this.sendPacket(playerDataPacket, true);
                player.transfer(transferPacket.address);
                break;
            case ProtocolInfo.PING_PACKET:
                this.sendPacket(PongPacket.create(), true);
                break;
            case ProtocolInfo.PLAYER_CLOSE_PACKET:
                PlayerClosePacket playerClosePacket = (PlayerClosePacket) packet;
                Player p = this.loader.getServer().getPlayer(playerClosePacket.playerName);
                if (p != null)
                {
                    String reason = playerClosePacket.reason;
                    cn.nukkit.network.protocol.DisconnectPacket dc = new cn.nukkit.network.protocol.DisconnectPacket();
                    dc.message = reason;
                    p.dataPacket(dc);
                }
                break;
            case ProtocolInfo.PLAYER_MESSAGE_PACKET:
                PlayerMessagePacket playerMessagePacket = (PlayerMessagePacket) packet;
                Player playerMessage = this.loader.getServer().getPlayer(playerMessagePacket.playerName);
                if (playerMessage != null)
                {
                    switch (playerMessagePacket.type)
                    {
                        case PlayerMessagePacket.TYPE_CHAT:
                            playerMessage.sendMessage(playerMessagePacket.message);
                            break;
                        case PlayerMessagePacket.TYPE_TITLE:
                            playerMessage.sendTitle(playerMessagePacket.message);
                            break;
                        case PlayerMessagePacket.TYPE_TIP:
                            playerMessage.sendTip(playerMessagePacket.message);
                            break;
                        case PlayerMessagePacket.TYPE_ACTION_BAR:
                            playerMessage.sendActionBar(playerMessagePacket.message);
                            break;
                    }
                }
                break;
        }
    }

    public long getServerId() {
        return serverId;
    }
}
