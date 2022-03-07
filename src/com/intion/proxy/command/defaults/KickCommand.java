package com.intion.proxy.command.defaults;

import com.intion.proxy.IntionPlayer;
import com.intion.proxy.Loader;
import com.intion.proxy.Session;
import com.intion.proxy.command.Command;
import com.intion.proxy.utils.Logger;

import java.net.InetSocketAddress;
import java.util.Arrays;

public class KickCommand extends Command {

    public KickCommand()
    {
        this.setName("kick");
    }

    @Override
    public void execute(Loader server, String[] args) {
        if (args.length < 2)
            return;
        String playerName = args[1];
        if (playerName.contains("&"))
            playerName = playerName.replaceAll("&", " ");
        String reason = "";
        if (args.length > 2)
        {
            String[] result = Arrays.copyOfRange(args, 2, args.length);
            StringBuilder builder = new StringBuilder();
            for (String v : result)
            {
                builder.append(v).append(" ");
            }
            reason = builder.toString();
        }
        if (reason.isEmpty())
            reason = "Kicked by Admin";

        for (Session session : server.getSessions().values())
        {
            if (!session.isInitialized())
                continue;
            IntionPlayer player = session.getPlayerByName(playerName);
            if (player != null)
            {
                player.close(reason);
                return;
            }
        }
        Logger.log("Not found " + playerName + ", try again.");
    }
}
