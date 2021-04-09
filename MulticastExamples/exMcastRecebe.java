import java.net.*;
import java.util.*;
/**
 *
 * @author joao
 */
public class exMcastRecebe {


   public static void main(String[] args) throws Exception 
   {
      InetAddress group = InetAddress.getByName("FF15::1");

      MulticastSocket ms = new MulticastSocket(9999);
      ms.joinGroup(group);
      byte[] buffer = new byte[8192];
      while(true){
         System.out.println("Waiting for a multicast message sent to FF15::1");
         DatagramPacket dp = new DatagramPacket(buffer, buffer.length); 
         ms.receive(dp);
         String s = new String(dp.getData(), 0, dp.getLength());
         String addr = new String(dp.getAddress().toString());
         System.out.println("Receive message " + s + " from " + addr  );
      }
   }
}
