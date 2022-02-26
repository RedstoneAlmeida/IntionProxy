package com.intion.proxy.network.protocol;

public class CommandPacket extends DataPacket {

    public static byte NETWORK_ID = ProtocolInfo.COMMAND_PACKET;

    public String command;
    public boolean all;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.command = this.getString();
        this.all = this.getBoolean();
    }

    @Override
    public void encode() {
        this.putString(this.command);
        this.putBoolean(this.all);
    }
}
