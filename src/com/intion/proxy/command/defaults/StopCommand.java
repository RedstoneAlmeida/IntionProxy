package com.intion.proxy.command.defaults;

import com.intion.proxy.Loader;
import com.intion.proxy.Session;
import com.intion.proxy.command.Command;
import com.intion.proxy.network.protocol.CommandPacket;
import com.intion.proxy.utils.Logger;

import java.util.Objects;

public class StopCommand extends Command {

    public StopCommand(){
        this.setName("stop");
    }

    @Override
    public void execute(Loader server, String[] args) {
        System.out.println("Stopping Proxy");
        if (args.length == 2 && args[1].equalsIgnoreCase("all"))
        {
            System.out.println("Stopping all servers connected with Proxy");
            CommandPacket pk = new CommandPacket();
            pk.command = "stop";
            pk.all = true;
            for (Session session : server.getSessions().values())
            {
                session.dataPacket(pk);
            }
        }
        server.getPluginLoader().call("onDisable");
        System.exit(0);
    }
}
