package com.intion.proxy.event.session;

import com.intion.proxy.Session;

import java.net.InetSocketAddress;

public class SessionInitializeEvent extends SessionEvent {

    private String serverName;
    private int slots;
    private InetSocketAddress address;

    public SessionInitializeEvent(Session session, int slots, String serverName, InetSocketAddress address) {
        super(session);
        this.serverName = serverName;
        this.slots = slots;
        this.address = address;
    }

    public String getServerName() {
        return serverName;
    }

    public int getSlots() {
        return slots;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public void setSlots(int slots) {
        this.slots = slots;
    }
}
