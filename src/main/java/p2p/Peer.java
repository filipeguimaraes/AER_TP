package p2p;

import java.net.InetAddress;
import java.time.LocalDateTime;


/**
 * Modulação de um peer conhecido pela rede p2p.
 */
public class Peer {

    private final InetAddress address;
    private final LocalDateTime addDate;
    private LocalDateTime timeStamp;
    private int status;

    public Peer(InetAddress address, LocalDateTime addDate) {
        this.address = address;
        this.addDate = addDate;
        this.timeStamp = LocalDateTime.now();
        this.status = Constantes.ON;
    }

    /**
     * Marcar como ativo o peer.
     */
    public void activate() {
        this.status = Constantes.ON;
        this.timeStamp = LocalDateTime.now();
    }

    /**
     * Marcar como ativo o peer.
     */
    public void deactivate() {
        this.status = Constantes.OFF;
    }


    public InetAddress getAddress() {
        return address;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public LocalDateTime getAddDate() {
        return addDate;
    }

    /**
     * Verifica se está ativo.
     *
     * @return true caso esteja ativo, falso caso contrário.
     */
    public boolean isON() {
        return this.status == Constantes.ON;
    }

    @Override
    public String toString() {
        String status = this.status == Constantes.ON ? "ON" : "OFF";
        return "{" +
                "address: " + address +
                ", status: " + status +
                '}';
    }
}
