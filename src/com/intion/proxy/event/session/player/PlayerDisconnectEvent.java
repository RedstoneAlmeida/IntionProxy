package com.intion.proxy.event.session.player;

import com.intion.proxy.IntionPlayer;
import com.intion.proxy.Session;

public class PlayerDisconnectEvent extends PlayerEvent {

    public PlayerDisconnectEvent(Session session, IntionPlayer player) {
        super(session, player);
    }

}
