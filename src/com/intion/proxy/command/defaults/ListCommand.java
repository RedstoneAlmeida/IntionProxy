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

        int players = 0;
        int maxPlayers = 0;

        int i = 0;
        for (long id : sessions.keySet())
        {
            Session session = sessions.get(id);
            if (!session.isInitialized()) continue;
            String f = String.format("%s (%s) with %s:%s [%s:%s] (%s/%s)", id
                    , session.getSessionName()
                    , session.getSocket().getInetAddress().getHostAddress()
                    , session.getSocket().getPort()
                    , session.getServerAddress().getAddress().getHostAddress()
                    , session.getServerAddress().getPort()
                    , session.getPlayerCount()
                    , session.getMaxPlayers());
            Logger.log(f);
            players += session.getPlayerCount();
            maxPlayers += session.getMaxPlayers();
            values[i] = f;
            i++;
        }
        Logger.log(String.format("%s players of %s total", players, maxPlayers));
        listPacket.list = values;
        server.dataPacket(listPacket);
    }
}
