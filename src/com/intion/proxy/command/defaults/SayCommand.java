package com.intion.proxy.command.defaults;

import com.intion.proxy.Loader;
import com.intion.proxy.Session;
import com.intion.proxy.command.Command;
import com.intion.proxy.network.protocol.CommandPacket;
import com.intion.proxy.utils.Logger;

public class SayCommand extends Command {

    public SayCommand() {
        this.setName("say");
    }

    @Override
    public void execute(Loader server, String[] args) {
        CommandPacket pk = new CommandPacket();

        StringBuilder builder = new StringBuilder();
        for (String arg : args)
        {
            builder.append(arg).append(" ");
        }
        pk.command = builder.toString();
        pk.all = true;
        for (Session session : server.getSessions().values())
        {
            session.dataPacket(pk);
        }
        Logger.log(pk.command.replace("say ", ""));
    }
}
