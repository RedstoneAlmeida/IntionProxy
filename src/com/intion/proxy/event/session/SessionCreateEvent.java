package com.intion.proxy.event.session;

import com.intion.proxy.Session;

public class SessionCreateEvent extends SessionEvent {

    private long sessionId;

    public SessionCreateEvent(Session session, long sessionId) {
        super(session);
        this.sessionId = sessionId;
    }

    public long getSessionId() {
        return sessionId;
    }
}
