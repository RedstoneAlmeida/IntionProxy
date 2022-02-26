package com.intion.proxy.event.session;

import com.intion.proxy.Session;
import com.intion.proxy.event.ProxyEvent;

public class SessionEvent extends ProxyEvent {

    private Session session;

    public SessionEvent(Session session) {
        this.session = session;
    }

    public Session getSession() {
        return session;
    }
}
