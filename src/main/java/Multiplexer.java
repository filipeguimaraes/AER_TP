import java.net.DatagramPacket;
import java.time.LocalDateTime;

public class Multiplexer {

    public static void receive(DatagramPacket dp){
        String message = new String(dp.getData());
        Rede rede = Rede.getInstance();
        switch (message){
            case "HELLO":
                rede.addPeer(new Peer(dp.getAddress(), LocalDateTime.now()));
                break;
            default:
                System.out.println("SHIT");
                rede.addPeer(new Peer(dp.getAddress(), LocalDateTime.now()));
                break;
        }

    }

}
