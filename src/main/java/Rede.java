import java.io.IOException;
import java.net.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class Rede {
    List<Peer> peers;

    public Rede() {
        this.peers = new ArrayList<>();
        receiveMulticast();
    }


    /**
     *
     *
     * @param group MULTICAST ADDRESS
     * @return Lista de endereços na rede
     */
    public static List<InetAddress> obtainValidAddresses(InetAddress group) throws SocketException {
        List<InetAddress> result = new ArrayList<>();

        //verify if group is a multicast address
        if (group == null || !group.isMulticastAddress()) return result;

        //obtain the network interfaces list
        Enumeration<NetworkInterface> ifs = NetworkInterface.getNetworkInterfaces();
        while (ifs.hasMoreElements()) {
            NetworkInterface ni = ifs.nextElement();
            //ignoring loopback, inactive interfaces and the interfaces that do not support multicast
            if (ni.isLoopback() || !ni.isUp() || !ni.supportsMulticast()) {
                continue;
            }
            Enumeration<InetAddress> addrs = ni.getInetAddresses();
            while (addrs.hasMoreElements()) {
                InetAddress addr = addrs.nextElement();
                //including addresses of the same type of group address
                if (group.getClass() != addr.getClass()) continue;
                if ((group.isMCLinkLocal() && addr.isLinkLocalAddress())
                        || (!group.isMCLinkLocal() && !addr.isLinkLocalAddress())) {
                    result.add(addr);
                }
            }
        }

        return result;
    }


    public void receiveMulticast() {
        new Thread(() -> {
            try {
                InetAddress group = InetAddress.getByName(Variables.MULTICAST_ADDRESS);

                MulticastSocket ms = new MulticastSocket(Variables.MULTICAST_PORT);
                ms.joinGroup(group);
                byte[] buffer = new byte[8192];
                while (true) {
                    //System.out.println("Waiting for a multicast message sent to" + Variables.MULTICAST_ADDRESS);
                    DatagramPacket dp = new DatagramPacket(buffer, buffer.length);

                    //bloqueia a espera de um host
                    ms.receive(dp);
                    String s = new String(dp.getData(), 0, dp.getLength());
                    String addr = dp.getAddress().toString();
                    System.out.println("Receive message " + s + " from " + addr);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }).start();
    }

    public void sendHellos() throws IOException {
        List<InetAddress> addrs = obtainValidAddresses(InetAddress.getByName(Variables.MULTICAST_ADDRESS));
        String msg = "HELLO";
        System.out.println("\nSending the message: " + msg);
        MulticastSocket ms = new MulticastSocket();
        DatagramPacket dp = new DatagramPacket(
                msg.getBytes(),
                msg.length(),
                InetAddress.getByName(Variables.MULTICAST_ADDRESS),
                Variables.MULTICAST_PORT);

        for (InetAddress addr: addrs) {
            System.out.println("Sending on " + addr);
            ms.setInterface(addr);
            ms.send(dp);
        }
        ms.close();
    }


}
