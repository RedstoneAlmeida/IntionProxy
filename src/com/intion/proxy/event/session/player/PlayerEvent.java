package com.intion.proxy.event.session.player;

import com.intion.proxy.IntionPlayer;
import com.intion.proxy.Session;
import com.intion.proxy.event.session.SessionEvent;

public class PlayerEvent extends SessionEvent {

    private IntionPlayer player;

    public PlayerEvent(Session session, IntionPlayer player) {
        super(session);
        this.player = player;
    }

    public IntionPlayer getPlayer() {
        return player;
    }
}
