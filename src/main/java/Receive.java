import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Receive {

    public static void main(String[] args) throws Exception
    {
        InetAddress group = InetAddress.getByName(Variables.MULTICAST_ADDRESS);

        MulticastSocket ms = new MulticastSocket(Variables.MULTICAST_PORT);
        ms.joinGroup(group);
        byte[] buffer = new byte[8192];
        while(true){
            System.out.println("Waiting for a multicast message sent to"+Variables.MULTICAST_ADDRESS);
            DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
            ms.receive(dp);
            String s = new String(dp.getData(), 0, dp.getLength());
            String addr = dp.getAddress().toString();
            System.out.println("Receive message " + s + " from " + addr  );
        }
    }
}
