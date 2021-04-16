import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.time.LocalDateTime;

public class Multiplexer {

    public static void receive(Message message,InetAddress originAddress){
        Network network = Network.getInstance();

        switch (message.getType()){
            case Variables.HELLO:
                //System.out.println("Multiplexer: Recebi um hello do peer "+dp.getAddress());
                network.addPeer(new Peer(originAddress, LocalDateTime.now()));
                break;
            case 2:
                break;
            default:
                System.out.println("Recebeu uma mensagem inválida!");;
                break;
        }

    }

}
