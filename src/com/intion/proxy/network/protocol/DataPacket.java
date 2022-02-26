package com.intion.proxy.network.protocol;

import com.intion.proxy.network.BinaryStream;

public abstract class DataPacket extends BinaryStream implements Cloneable{

    public abstract byte pid();

    public abstract void decode();

    public abstract void encode();

    @Override
    public DataPacket reset() {
        super.reset();
        this.putByte(this.pid());
        this.putShort(0);
        return this;
    }

    public DataPacket clean() {
        this.setBuffer(null);
        this.setOffset(0);
        return this;
    }

    @Override
    public DataPacket clone() {
        try {
            return (DataPacket) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
