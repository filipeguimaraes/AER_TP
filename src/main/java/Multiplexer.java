import java.io.IOException;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.List;

public class Multiplexer {

    public static void receive(Message message, InetAddress originAddress) throws IOException {
        Network network = Network.getInstance();

        switch (message.getType()) {
            case Variables.HELLO:
                if (!originAddress.toString().contains("127.0.0.1")) {
                    network.addPeer(new Peer(originAddress, LocalDateTime.now()));
                }
                break;
            case Variables.QUERY:
                network.searchFile(message.getMessage(), originAddress);
                break;
            case Variables.QUERY_RESPONSE:
                network.sourcePeerFile(message.getMessage(), originAddress);
            case Variables.PING:
                Message hello = new Message(Variables.HELLO, null);
                network.sendSimpleMessage(hello, originAddress);
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
            case Variables.REQUEST:
                FileTransfer.send(message.getMessage());
                break;
            default:
                System.out.println("Recebeu uma mensagem inválida!");
                break;
        }

    }

}
