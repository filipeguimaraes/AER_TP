package dtn;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Receiver {

    public static void receiveMulticast() {
        new Thread(() -> {
            try {
                InetAddress group = InetAddress.getByName(Constants.MULTICAST_ADDRESS);
                MulticastSocket ms = new MulticastSocket(Constants.MULTICAST_PORT);
                ms.joinGroup(group);

                while (true) {
                    byte[] buffer = new byte[8192];
                    DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
                    ms.receive(dp);

                    ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
                    ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(bais));
                    Message message = (Message) ois.readObject();

                    Receiver.work(message, dp.getAddress());
                }
            } catch (Exception e) {
                System.out.println("Receive Multicast:" + e.getMessage());
            }

        }).start();
    }

    private static void work(Message message, InetAddress originAddress) {
        DTN dtn = DTN.getInstance();
        message.getPath().add(originAddress);
        switch (message.getType()) {
            case Constants.INTEREST:
                dtn.receiveInterest(message);
                break;
            case Constants.POST:
                break;
            default:
                System.out.println("Recebeu uma mensagem inválida!");
                break;
        }
    }


}
