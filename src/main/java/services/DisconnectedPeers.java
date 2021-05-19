package services;

import network.Network;
import network.Peer;
import network.Variables;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ValueRange;
import java.util.ArrayList;

public class DisconnectedPeers {

    private boolean flag;
    private static DisconnectedPeers instance = null;

    private DisconnectedPeers() {
        this.flag = true;
    }

    public static DisconnectedPeers getInstance(){
        if (instance == null){
            instance = new DisconnectedPeers();
        }
        return instance;
    }

    /**
     * Serviço para regularmente pesquisar por peers desconectados, ou seja,
     * com um timestamp mais antigo do que o predefinido.
     */
    public void killPeers() {
        Network network = Network.getInstance();

        new Thread(() -> {
            while (flag) {
                try {
                    network.lock();
                    ArrayList<Peer> peers = new ArrayList<>(network.getPeers().values());
                    for (Peer peer : peers) {
                        if (peer.isON()) {
                            Duration duration = Duration.between(peer.getTimeStamp(), LocalDateTime.now());
                            if (duration.toMillis() > Variables.DEAD_TIME) {
                                System.out.println("(" + LocalDateTime.now() +
                                        ") Network.Peer desconectado: " + peer.getAddress().toString());
                                network.getPeers().get(peer.getAddress().toString()).deactivate();
                            }
                        }
                    }
                } finally {
                    network.unlock();
                }
                try {
                    Thread.sleep(Variables.HELLO_TIME);
                } catch (Exception ignored) {
                    System.out.println("Something went wrong!");
                }

            }
        }).start();
    }

    public void init(){
        this.flag = true;
        killPeers();
    }

    public void stop(){
        this.flag = false;
    }

    public String getState(){
        return this.flag ? "ON" : "OFF";
    }


}
