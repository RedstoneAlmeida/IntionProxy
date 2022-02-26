package com.intion.proxy.command.defaults;

import com.intion.proxy.Loader;
import com.intion.proxy.Session;
import com.intion.proxy.command.Command;
import com.intion.proxy.network.protocol.ListPacket;
import com.intion.proxy.utils.Logger;

import java.util.Map;

public class ListCommand extends Command {

    public ListCommand() {
        this.setName("list");
    }

    @Override
    public void execute(Loader server) {
        Map<Long, Session> sessions = server.getSessions();
        if (sessions.isEmpty())
        {
            Logger.log("List: Empty");
            return;
        }

        Logger.log("List: ");
        ListPacket listPacket = new ListPacket();
        String[] values = new String[sessions.size()];
        int i = 0;
        for (long id : sessions.keySet())
        {
            Session session = sessions.get(id);
            String f = String.format("%s with %s:%s (%s/%s)", id
                    , session.getSocket().getInetAddress().getHostAddress()
                    , session.getSocket().getPort()
                    , session.getPlayerCount()
                    , session.getMaxPlayers());
            Logger.log(f);
            values[i] = f;
            i++;
        }
        listPacket.list = values;
        server.dataPacket(listPacket);
    }
}
