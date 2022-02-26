package com.intion.proxy.command.defaults;

import com.intion.proxy.Loader;
import com.intion.proxy.Session;
import com.intion.proxy.command.Command;
import com.intion.proxy.network.protocol.CommandPacket;
import com.intion.proxy.utils.Logger;

import java.util.Objects;

public class StopCommand extends Command {

    private Loader loader;
    private boolean all;

    public StopCommand()
    {
        this(false, null);
    }

    public StopCommand(boolean all, Loader loader){
        this.setName("stop");
        this.all = all;
        this.loader = loader;
    }

    @Override
    public void execute(Loader server) {
        System.out.println("Stopping Proxy");
        if (this.all)
        {
            System.out.println("Stopping all servers connected with Proxy");
            CommandPacket pk = new CommandPacket();
            pk.command = "stop";
            pk.all = true;
            for (Session session : loader.getSessions().values())
            {
                session.dataPacket(pk);
            }
        }
        server.getPluginLoader().call("onDisable");
        System.exit(0);
    }
}
