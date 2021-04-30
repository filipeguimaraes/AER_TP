package services;

import network.Message;
import network.Multiplexer;
import network.Variables;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Receiver {
    public static Receiver instance = null;
    public boolean flag;

    private Receiver() {
        this.flag = true;
    }

    public static Receiver getInstance(){
        if (instance == null){
            instance = new Receiver();
        }
        return instance;
    }

    /**
     * Serviço para estar à escuta de mensagens.
     */
    public void receiveMulticast() {
        new Thread(() -> {
            try {
                InetAddress group = InetAddress.getByName(Variables.MULTICAST_ADDRESS);
                MulticastSocket ms = new MulticastSocket(Variables.MULTICAST_PORT);
                ms.joinGroup(group);


                while (flag) {
                    byte[] buffer = new byte[8192];
                    DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
                    ms.receive(dp);

                    ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
                    ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(bais));
                    Message message = (Message) ois.readObject();

                    Multiplexer.receive(message, dp.getAddress());
                }
            } catch (Exception e) {
                System.out.println("Receive Multicast:" + e.getMessage());
            }

        }).start();
    }

    public void init(){
        this.flag = true;
        receiveMulticast();
    }

    public void stop(){
        this.flag = false;
    }

    public String getState(){
        return this.flag ? "ON" : "OFF";
    }




}
