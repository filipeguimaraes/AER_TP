import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.List;

public class Peer {
    private final InetAddress address;
    private final LocalDateTime addDate;
    private List<PeerCriteria> criteria;

    public Peer(InetAddress address, LocalDateTime addDate) {
        this.address = address;
        this.addDate = addDate;
    }

    public InetAddress getAddress() {
        return address;
    }

    public LocalDateTime getAddDate() {
        return addDate;
    }
}
