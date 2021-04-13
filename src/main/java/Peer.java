import java.net.InetAddress;
import java.time.LocalDateTime;

public class Peer {
    private final InetAddress address;
    private final LocalDateTime addDate;

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
