import java.io.IOException;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.List;

public class Multiplexer {

    public static void receive(Message message, InetAddress originAddress) throws IOException {
        Network network = Network.getInstance();

        switch (message.getType()) {
            case Variables.HELLO:
                network.addPeer(new Peer(originAddress, LocalDateTime.now()));
                break;
            case Variables.QUERY:
                System.out.println("Recebi uma query!");
                break;
            case Variables.PING:
                Message hello = new Message(Variables.HELLO, null);
                network.sendSimpleMessage(hello, InetAddress.getByName(message.getMessage()));
                break;
            case Variables.PEERS:
                System.out.println("Recebi peers");
                List<InetAddress> peers = null;
                try {
                    peers = message.getPeers();
                } catch (Exception e) {
                    System.out.println("receive: " + e.getMessage());
                }
                network.addPeers(peers);
                break;
            default:
                System.out.println("Recebeu uma mensagem inválida!");
                break;
        }

    }

}
