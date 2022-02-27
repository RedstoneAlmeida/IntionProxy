package com.intion.proxy.command.defaults;

import com.intion.proxy.IntionPlayer;
import com.intion.proxy.Loader;
import com.intion.proxy.Session;
import com.intion.proxy.command.Command;
import com.intion.proxy.utils.Logger;

import java.net.InetSocketAddress;

public class TransferCommand extends Command {

    public TransferCommand() {
        this.setName("transfer");
    }

    @Override
    public void execute(Loader loader, String[] args) {
        if (args.length < 3)
            return;
        String playerName = args[1];
        String serverName = args[2];
        Session server = loader.getSessionByName(serverName);
        if (server == null)
        {
            Logger.log("Server by " + serverName + " is not found");
            return;
        }
        try {
            for (Session session : loader.getSessions().values())
            {
                if (!session.isInitialized())
                    continue;
                IntionPlayer player = session.getPlayerByName(playerName);
                if (player != null)
                {
                    InetSocketAddress address = server.getServerAddress();
                    if (address.getHostName().equalsIgnoreCase("0.0.0.0"))
                        address = new InetSocketAddress(session.getSocket().getInetAddress().getHostAddress(), address.getPort());
                    player.transfer(address, serverName);
                    break;
                }
            }
        } catch (Exception e)
        {
            Logger.log("Erro: definição de porta invalido");
        }
    }
}
