package com.intion.proxy.event.session.player;

import com.intion.proxy.IntionPlayer;
import com.intion.proxy.Session;

public class PlayerConnectEvent extends PlayerEvent {

    public PlayerConnectEvent(Session session, IntionPlayer player) {
        super(session, player);
    }

}
