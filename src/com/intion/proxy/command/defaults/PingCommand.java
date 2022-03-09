package com.intion.proxy.command.defaults;

import com.intion.proxy.IntionPlayer;
import com.intion.proxy.Loader;
import com.intion.proxy.Session;
import com.intion.proxy.command.Command;
import com.intion.proxy.network.protocol.PingPacket;
import com.intion.proxy.utils.Logger;
import com.intion.proxy.utils.LoggerEnum;

import java.net.InetSocketAddress;

public class PingCommand extends Command {

    public PingCommand()
    {
        this.setName("ping");
    }

    @Override
    public void execute(Loader loader, String[] args) {
        if (args.length < 2)
            return;
        String serverName = args[1];
        Session server = loader.getSessionByName(serverName);
        if (server == null || !server.isInitialized())
        {
            Logger.log("Server by " + serverName + " is not found");
            return;
        }
        server.sendPing();
        LoggerEnum.COMMAND.log("Requesting information from " + server.getSessionName());
    }

}
