package com.intion.proxy.network.protocol;

public interface ProtocolInfo {

    byte HANDLER_PACKET = 0x01;
    byte CONNECTION_PACKET = 0x02;
    byte DISCONNECTION_PACKET = 0x03;
    byte INFORMATION_PACKET = 0x04;
    byte SERVER_INFORMATION_PACKET = 0x05;
    byte CONFIRMATION_PACKET = 0x06;
    byte COMMAND_PACKET = 0x07;
    byte LIST_PACKET = 0x08;
    byte PLAYER_DATA_PACKET = 0x09;
    byte STORE_DATA_PACKET = 0x10;
    byte TRANSFER_PACKET = 0x11;
    byte PING_PACKET = 0x12;
    byte PONG_PACKET = 0x13;
    byte PLAYER_CLOSE_PACKET = 0x14;
    byte PLAYER_MESSAGE_PACKET = 0x15;

}
