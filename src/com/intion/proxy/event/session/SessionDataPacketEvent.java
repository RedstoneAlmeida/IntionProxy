package com.intion.proxy.event.session;

import com.intion.proxy.Session;
import com.intion.proxy.network.protocol.DataPacket;

public class SessionDataPacketEvent extends SessionEvent {

    private DataPacket packet;

    public SessionDataPacketEvent(Session session, DataPacket packet) {
        super(session);
        this.packet = packet;
    }

    public DataPacket getPacket() {
        return packet;
    }
}
