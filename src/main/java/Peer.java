import java.net.InetAddress;
import java.time.LocalDateTime;

public class Peer {
    private InetAddress address;
    private LocalDateTime addDate;

    public Peer(InetAddress endereco, LocalDateTime dataCriacao) {
        this.address = endereco;
        this.addDate = dataCriacao;
    }

    public InetAddress getAddress() {
        return address;
    }

    public LocalDateTime getAddDate() {
        return addDate;
    }
}
