package com.intion.proxy.network.protocol;

/**
 * Created by ASUS on 19/02/2018.
 */
public class InformationPacket extends DataPacket {

    public static byte NETWORK_ID = ProtocolInfo.INFORMATION_PACKET;

    public String information;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.information = this.getString();
    }

    @Override
    public void encode() {
        this.putString(information);
    }
}
