package services;

import p2p.Constantes;
import p2p.P2P;
import p2p.Peer;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Serviço para regularmente pesquisar por peers desconectados, ou seja,
 * com um timestamp mais antigo do que o predefinido.
 */
public class DisconnectedPeers {

    private static DisconnectedPeers instance = null;
    private boolean flag;

    private DisconnectedPeers() {
        this.flag = true;
    }

    public static DisconnectedPeers getInstance() {
        if (instance == null) {
            instance = new DisconnectedPeers();
        }
        return instance;
    }

    /**
     * Serviço para regularmente pesquisar por peers desconectados, ou seja,
     * com um timestamp mais antigo do que o predefinido.
     */
    public void killPeers() {
        P2P p2P = P2P.getInstance();

        new Thread(() -> {
            while (flag) {
                try {
                    p2P.lock();
                    ArrayList<Peer> peers = new ArrayList<>(p2P.getPeers().values());
                    for (Peer peer : peers) {
                        if (peer.isON()) {
                            Duration duration = Duration.between(peer.getTimeStamp(), LocalDateTime.now());
                            if (duration.toMillis() > Constantes.DEAD_TIME) {
                                //System.out.println("(" + LocalDateTime.now() +") P2P.Peer desconectado: " + peer.getAddress().toString());
                                p2P.getPeers().get(peer.getAddress().toString()).deactivate();
                            }
                        }
                    }
                } finally {
                    p2P.unlock();
                }
                try {
                    Thread.sleep(Constantes.HELLO_TIME);
                } catch (Exception ignored) {
                    System.out.println("Something went wrong!");
                }

            }
        }).start();
    }

    public void init() {
        this.flag = true;
        killPeers();
    }

    public void stop() {
        this.flag = false;
    }

    public String getState() {
        return this.flag ? "ON" : "OFF";
    }


}
