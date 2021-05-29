package services;

import network.Message;
import network.P2P;
import network.Peer;
import network.Variables;
import services.FileTransfer;

import java.io.IOException;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.List;

public class Multiplexer {

    /**
     * Método para tratar as mensagens recebidas chamando os métodos respetivos.
     * @param message Mensagem recebida
     * @param originAddress Endereço de origem.
     * @throws IOException Exceção.
     */
    public static void receive(Message message, InetAddress originAddress) throws IOException {
        P2P p2P = P2P.getInstance();

        switch (message.getType()) {
            case Variables.HELLO:
                if (!originAddress.toString().contains("127.0.0.1")) {
                    p2P.addPeer(new Peer(originAddress, LocalDateTime.now()));
                }
                break;
            case Variables.QUERY:
                p2P.searchFile(message.getMessage(), originAddress);
                break;
            case Variables.QUERY_RESPONSE:
                p2P.sourcePeerFile(message.getMessage(), originAddress);
            case Variables.PING:
                if (!originAddress.toString().contains("127.0.0.1")) {
                    p2P.addPeer(new Peer(originAddress, LocalDateTime.now()));
                }
                Message hello = new Message(Variables.HELLO, null);
                p2P.sendSimpleMessage(hello, originAddress);
                break;
            case Variables.PEERS:
                System.out.println("Recebi peers");
                List<InetAddress> peers = null;
                try {
                    peers = message.getPeers();
                } catch (Exception e) {
                    System.out.println("receive: " + e.getMessage());
                }
                p2P.addPeers(peers);
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
