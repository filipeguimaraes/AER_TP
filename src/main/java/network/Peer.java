package network;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.List;

public class Peer {

    private final InetAddress address;
    private final LocalDateTime addDate;
    private LocalDateTime timeStamp;
    private int status;
    private List<PeerCriteria> criteria;

    public Peer(InetAddress address, LocalDateTime addDate) {
        this.address = address;
        this.addDate = addDate;
        this.timeStamp = LocalDateTime.now();
        this.status = Variables.ON;
    }

    /**
     * Marcar como ativo o peer.
     */
    public void activate() {
        this.status = Variables.ON;
        this.timeStamp = LocalDateTime.now();
    }

    /**
     * Marcar como ativo o peer.
     */
    public void deactivate() {
        this.status = Variables.OFF;
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
     * @return true caso esteja ativo, falso caso contrário.
     */
    public boolean isON(){
        return this.status == Variables.ON;
    }

    @Override
    public String toString() {
        String status = this.status == Variables.ON ? "ON" : "OFF";
        return "{" +
                "address: " + address +
                ", status: " + status  +
                '}';
    }
}
