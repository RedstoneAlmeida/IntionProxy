package com.intion.proxy.event.session;

import com.intion.proxy.Session;
import com.intion.proxy.event.Cancelable;
import com.intion.proxy.network.protocol.DataPacket;

public class SessionDataPacketSendEvent extends SessionDataPacketEvent implements Cancelable {

    public SessionDataPacketSendEvent(Session session, DataPacket packet) {
        super(session, packet);
    }

}
