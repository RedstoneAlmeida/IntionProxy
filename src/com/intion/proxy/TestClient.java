package com.intion.proxy;

import com.intion.proxy.network.protocol.ConnectionPacket;
import com.intion.proxy.network.protocol.DataPacket;
import com.intion.proxy.network.protocol.HandlerPacket;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TestClient {

    public static void main(String[] args) {
        try {
            Socket s = new Socket("0.0.0.0", 25567);

            ConnectionPacket pk = new ConnectionPacket();
            pk.name = "Teste";
            pk.password = "Teste2";
            pk.slots = 30;

            dataPacket(pk, s);

            new Thread(() -> {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    dataPacket(pk, s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            while (true)
            {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                dataPacket(new HandlerPacket(), s);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void dataPacket(DataPacket packet, Socket s) throws IOException
    {
        packet.encode();
        DataOutputStream saida = new DataOutputStream(s.getOutputStream());
        saida.writeByte(packet.pid());
        byte[] buffered = packet.getBuffer();
        saida.writeInt(buffered.length);
        saida.write(buffered);
    }

}
