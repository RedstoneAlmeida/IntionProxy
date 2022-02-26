package com.intion.proxy.plugin;

import cn.nukkit.Player;
import cn.nukkit.command.ConsoleCommandSender;
import com.intion.proxy.network.protocol.*;
import com.intion.proxy.utils.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Session extends Thread {

    private Socket socket = null;
    private Loader loader;
    private long serverId = -1;

    private String address, password;
    private int port;

    private int tries = 0;

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

            ConnectionPacket pk = new ConnectionPacket();
            pk.name = this.loader.getConfig().getString("name", "Unknown");
            pk.password = this.password;
            pk.slots = this.loader.getServer().getMaxPlayers();

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
                this.sendPacket(playerDataPacket);
                player.transfer(transferPacket.address);
                break;
        }
    }

    public long getServerId() {
        return serverId;
    }
}
