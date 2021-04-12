import java.net.DatagramPacket;
import java.net.InetAddress;
import java.time.LocalDateTime;

public class Multiplexer {

    public static void receive(String message, InetAddress address){
        Rede rede = Rede.getInstance();
        switch (message){
            case "HELLO":
                rede.addPeer(new Peer(address, LocalDateTime.now()));
                break;
            default:
                System.out.println("Error");;
                break;
        }

    }

}
