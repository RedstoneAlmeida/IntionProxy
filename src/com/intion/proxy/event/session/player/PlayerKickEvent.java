package com.intion.proxy.event.session.player;

import com.intion.proxy.IntionPlayer;
import com.intion.proxy.Session;

public class PlayerKickEvent extends PlayerEvent {

    public static int BANNED = 0;
    public static int OTHER = 1;

    private int type;
    private String reason;

    public PlayerKickEvent(Session session, IntionPlayer player, int type, String reason) {
        super(session, player);
        this.type = type;
        this.reason = reason;
    }

    public int getType() {
        return type;
    }

    public String getReason() {
        return reason;
    }
}
