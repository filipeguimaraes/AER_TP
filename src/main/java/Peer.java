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

    public void activate() {
        this.status = Variables.ON;
        this.timeStamp = LocalDateTime.now();
    }

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

    public boolean isON(){
        return this.status == Variables.ON;
    }
}
