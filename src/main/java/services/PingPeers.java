package services;

import network.Message;
import network.Network;
import network.Peer;
import network.Variables;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PingPeers {

    private boolean flag;
    private static PingPeers instance = null;

    private PingPeers() {
        this.flag = true;
    }

    public static PingPeers getInstance(){
        if (instance == null){
            instance = new PingPeers();
        }
        return instance;
    }


    /**
     * Função para enviar mensagens para verificar se algum peer se desconectou.
     */
    public void sendPings() {
        Network network = Network.getInstance();
        new Thread(() -> {
            while (true) {
                try {
                    network.lock();
                    List<Peer> peers = new ArrayList<>(network.getPeers().values());
                    Message ping = new Message(Variables.PING, null);
                    for (Peer p : peers) {
                        try {
                            network.sendSimpleMessage(ping, p.getAddress());
                        } catch (IOException e) {
                            network.getPeers().get(p.getAddress().toString()).deactivate();
                            //System.out.println("Não foi possível enviar o ping para " + p.getAddress() + ".");
                        }
                    }
                } finally {
                    network.unlock();
                }
                try {
                    Thread.sleep(Variables.HELLO_TIME);
                } catch (Exception e) {
                    System.out.println("sendPings: " + e.getMessage());
                }
            }
        }).start();
    }

    public void init(){
        this.flag = true;
        sendPings();
    }

    public void stop(){
        this.flag = false;
    }

    public String getState(){
        return this.flag ? "ON" : "OFF";
    }

}
