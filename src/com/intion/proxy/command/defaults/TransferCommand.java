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
    public void execute(Loader server, String[] args) {
        if (args.length < 5)
            return;
        String playerName = args[1];
        String serverName = args[2];
        String host = args[3];
        try {
            int port = Integer.parseInt(args[4]);

            for (Session session : server.getSessions().values())
            {
                IntionPlayer player = session.getPlayerByName(playerName);
                if (player != null)
                {
                    player.transfer(new InetSocketAddress(host, port), serverName);
                    break;
                }
            }
        } catch (Exception e)
        {
            Logger.log("Erro: definição de porta invalido");
        }
    }
}
