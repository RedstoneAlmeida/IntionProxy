package com.intion.proxy.event.session.player;

import com.intion.proxy.IntionPlayer;
import com.intion.proxy.Session;

public class PlayerTransferEvent extends PlayerEvent {

    public PlayerTransferEvent(Session session, IntionPlayer player) {
        super(session, player);
    }

}
