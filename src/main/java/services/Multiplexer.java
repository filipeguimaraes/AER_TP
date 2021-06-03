package services;

import p2p.Constantes;
import p2p.Message;
import p2p.P2P;
import p2p.Peer;

import java.io.IOException;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Tratar os diferentes tipos de mensagem
 */
public class Multiplexer {

    /**
     * Método para tratar as mensagens recebidas chamando os métodos respetivos.
     *
     * @param message       Mensagem recebida
     * @param originAddress Endereço de origem.
     * @throws IOException Exceção.
     */
    public static void receive(Message message, InetAddress originAddress) throws IOException {
        P2P p2P = P2P.getInstance();

        switch (message.getType()) {
            case Constantes.HELLO:
                if (!originAddress.toString().contains("127.0.0.1")) {
                    p2P.addPeer(new Peer(originAddress, LocalDateTime.now()));
                }
                break;
            case Constantes.PING:
                if (!originAddress.toString().contains("127.0.0.1")) {
                    p2P.addPeer(new Peer(originAddress, LocalDateTime.now()));
                }
                Message hello = new Message(Constantes.HELLO, null);
                p2P.sendSimpleMessage(hello, originAddress);
                break;
            case Constantes.PEERS:
                System.out.println("Recebi peers");
                List<InetAddress> peers = null;
                try {
                    peers = message.getPeers();
                } catch (Exception e) {
                    System.out.println("receive: " + e.getMessage());
                }
                p2P.addPeers(peers);
                break;
            default:
                System.out.println("Recebeu uma mensagem inválida!");
                break;
        }

    }

}
