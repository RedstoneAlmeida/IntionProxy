package com.intion.proxy.network;

import com.intion.proxy.Loader;
import com.intion.proxy.network.protocol.*;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by ASUS on 19/02/2018.
 */
public class Network {

    @SuppressWarnings("unchecked")
    private Class<? extends DataPacket>[] packetPool = new Class[256];

    public Network(){
        this.registerPackets();
    }

    public DataPacket getPacket(byte id) {
        Class<? extends DataPacket> clazz = this.packetPool[id & 0xff];
        if (clazz != null) {
            try {
                return clazz.newInstance();
            } catch (Exception e) {
                System.out.println("Logger");
            }
        }
        return null;
    }

    public void registerPacket(byte id, Class<? extends DataPacket> clazz) {
        this.packetPool[id & 0xff] = clazz;
    }

    public void registerPackets(){
        this.registerPacket(ProtocolInfo.CONNECTION_PACKET, ConnectionPacket.class);
        this.registerPacket(ProtocolInfo.CONFIRMATION_PACKET, ConfirmationPacket.class);
        this.registerPacket(ProtocolInfo.HANDLER_PACKET, HandlerPacket.class);
        this.registerPacket(ProtocolInfo.DISCONNECTION_PACKET, DisconnectPacket.class);
        this.registerPacket(ProtocolInfo.INFORMATION_PACKET, InformationPacket.class);
        this.registerPacket(ProtocolInfo.SERVER_INFORMATION_PACKET, ServerInformationPacket.class);
        this.registerPacket(ProtocolInfo.COMMAND_PACKET, CommandPacket.class);
        this.registerPacket(ProtocolInfo.LIST_PACKET, ListPacket.class);
        this.registerPacket(ProtocolInfo.PLAYER_DATA_PACKET, PlayerDataPacket.class);
        this.registerPacket(ProtocolInfo.TRANSFER_PACKET, TransferPacket.class);
        this.registerPacket(ProtocolInfo.PING_PACKET, PingPacket.class);
    }

}
