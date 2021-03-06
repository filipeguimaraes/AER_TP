package services;

import p2p.Constantes;
import p2p.Message;
import p2p.P2P;
import p2p.Peer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Serviço para enviar mensagens para verificar se algum peer se desconectou.
 */
public class PingPeers {

    private static PingPeers instance = null;
    private boolean flag;

    private PingPeers() {
        this.flag = true;
    }

    public static PingPeers getInstance() {
        if (instance == null) {
            instance = new PingPeers();
        }
        return instance;
    }


    /**
     * Função para enviar mensagens para verificar se algum peer se desconectou.
     */
    public void sendPings() {
        P2P p2P = P2P.getInstance();
        new Thread(() -> {
            while (true) {
                try {
                    p2P.lock();
                    List<Peer> peers = new ArrayList<>(p2P.getPeers().values());
                    Message ping = new Message(Constantes.PING, null);
                    for (Peer p : peers) {
                        try {
                            p2P.sendSimpleMessage(ping, p.getAddress());
                        } catch (IOException e) {
                            p2P.getPeers().get(p.getAddress().toString()).deactivate();
                            //System.out.println("Não foi possível enviar o ping para " + p.getAddress() + ".");
                        }
                    }
                } finally {
                    p2P.unlock();
                }
                try {
                    Thread.sleep(Constantes.HELLO_TIME);
                } catch (Exception e) {
                    System.out.println("sendPings: " + e.getMessage());
                }
            }
        }).start();
    }

    public void init() {
        this.flag = true;
        sendPings();
    }

    public void stop() {
        this.flag = false;
    }

    public String getState() {
        return this.flag ? "ON" : "OFF";
    }

}
