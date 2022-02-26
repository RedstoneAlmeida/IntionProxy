package com.intion.proxy.network.protocol;

import cn.nukkit.Player;

public class PlayerDataPacket extends DataPacket{

    public static byte NETWORK_ID = ProtocolInfo.PLAYER_DATA_PACKET;

    public int type = PlayerDataType.CONNECT;

    public String username;
    public String xuid;
    public String serverName;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.type = this.getInt();
        this.username = this.getString();
        this.xuid = this.getString();
        this.serverName = this.getString();
    }

    @Override
    public void encode() {
        this.putInt(this.type);
        this.putString(this.username);
        this.putString(this.xuid);
        this.putString(this.serverName);
    }

    public interface PlayerDataType {

        int CONNECT = 0;
        int DISCONNECT = 1;
        int CHANGE_SERVER = 2;

    }
}
