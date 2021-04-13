import java.net.DatagramPacket;
import java.net.InetAddress;
import java.time.LocalDateTime;

public class Multiplexer {

    public static void receive(DatagramPacket dp){
        Network network = Network.getInstance();
        String message = new String(dp.getData(), 0, dp.getLength());
        switch (message){
            case "HELLO":
                network.addPeer(new Peer(dp.getAddress(), LocalDateTime.now()));
                break;
            default:
                System.out.println("Recebeu uma mensagem inválida!");;
                break;
        }

    }

}
