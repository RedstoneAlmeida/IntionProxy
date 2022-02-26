package com.intion.proxy.network.protocol;

import java.util.List;

public class ListPacket extends DataPacket {

    public static byte NETWORK_ID = ProtocolInfo.LIST_PACKET;

    public String[] list;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        int val = this.getInt();
        this.list = new String[val];
        for (int i = 0; i < val; i++)
        {
            this.list[i] = this.getString();
        }
    }

    @Override
    public void encode() {
        this.putInt(list.length);
        for (String sec : list)
        {
            this.putString(sec);
        }
    }
}