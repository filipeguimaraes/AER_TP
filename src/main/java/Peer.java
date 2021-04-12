import java.net.InetAddress;
import java.time.LocalDateTime;

public class Peer {
    private InetAddress endereco;
    private LocalDateTime dataCriacao; //opcional

    public Peer(InetAddress endereco, LocalDateTime dataCriacao) {
        this.endereco = endereco;
        this.dataCriacao = dataCriacao;
    }

    public InetAddress getEndereco() {
        return endereco;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
}
