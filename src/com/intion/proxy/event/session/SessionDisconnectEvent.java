package com.intion.proxy.event.session;

import com.intion.proxy.Session;

public class SessionDisconnectEvent extends SessionEvent {

    private String cause;

    public SessionDisconnectEvent(Session session, String cause) {
        super(session);
        this.cause = cause;
    }

    public String getCause() {
        return cause;
    }
}
