package com.intion.proxy.network.protocol;

public class PlayerMessagePacket extends DataPacket {

    public static byte NETWORK_ID = ProtocolInfo.PLAYER_MESSAGE_PACKET;
    
    public final static int TYPE_CHAT = 0;
    public final static int TYPE_TITLE = 1;
    public final static int TYPE_TIP = 2;
    public final static int TYPE_ACTION_BAR = 3;

    public String playerName;
    public String message;
    public int type = TYPE_CHAT;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.playerName = this.getString();
        this.message = this.getString();
        this.type = this.getInt();
    }

    @Override
    public void encode() {
        this.putString(this.playerName);
        this.putString(this.message);
        this.putInt(this.type);
    }

    public static PlayerMessagePacket create(String playerName, String message)
    {
        return create(playerName, message, TYPE_CHAT);
    }

    public static PlayerMessagePacket create(String playerName, String message, int type)
    {
        PlayerMessagePacket packet = new PlayerMessagePacket();
        packet.playerName = playerName;
        packet.message = message;
        packet.type = type;
        return packet;
    }
    
}
