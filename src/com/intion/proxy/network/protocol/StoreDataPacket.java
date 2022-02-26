package com.intion.proxy.network.protocol;

public class StoreDataPacket extends DataPacket{

    public static byte NETWORK_ID = ProtocolInfo.STORE_DATA_PACKET;

    /*
    Example:
    dataName = "skywars_points"
    userName = "RedstoneAlmeida"
    storeType = 0
    typeData = GET_DATA
     */

    public String dataName;
    public String userName;
    public int storeType = StoreType.CONFIG_YML;
    public int typeData = StoreType.SET_DATA;
    public String data = ""; // only on decode

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.dataName = this.getString();
        this.userName = this.getString();
        this.storeType = this.getInt();
        this.typeData = this.getInt();
        this.data = this.getString();
    }

    @Override
    public void encode() {
        this.putString(this.dataName);
        this.putString(this.userName);
        this.putInt(this.storeType);
        this.putInt(this.typeData);
        this.putString(this.data);
    }

    public interface StoreType {

        int CONFIG_YML = 0;
        int CONFIG_JSON = 1;

        int SET_DATA = 0;
        int GET_DATA = 1;

    }
}
