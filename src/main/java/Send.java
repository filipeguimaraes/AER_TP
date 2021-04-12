import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class Send {


    /**
     * Encontra endereços na rede p2p
     * @param group MULTICAST ADDRESS
     * @return Lista de endereços na rede
     */
    public static List<InetAddress> obtainValidAddresses(InetAddress group) {
        List<InetAddress> result = new ArrayList<>();

        System.out.println("\nObtain valid addresses according to group address");
        //verify if group is a multicast address
        if (group == null || !group.isMulticastAddress()) return result;
        try {
            //obtain the network interfaces list
            Enumeration<NetworkInterface> ifs = NetworkInterface.getNetworkInterfaces();
            while (ifs.hasMoreElements()) {
                NetworkInterface ni = ifs.nextElement();
                //ignoring loopback, inactive interfaces and the interfaces that do not support multicast
                if (ni.isLoopback() || !ni.isUp() || !ni.supportsMulticast()) {
                    System.out.println("Ignoring Interface: " + ni.getDisplayName());
                    continue;
                }
                Enumeration<InetAddress> addrs = ni.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    InetAddress addr = addrs.nextElement();
                    //including addresses of the same type of group address
                    if (group.getClass() != addr.getClass()) continue;
                    if ((group.isMCLinkLocal() && addr.isLinkLocalAddress())
                            || (!group.isMCLinkLocal() && !addr.isLinkLocalAddress())) {
                        System.out.println("Interface: " + ni.getDisplayName() + " Address: " +addr);
                        result.add(addr);
                    } else {
                        System.out.println("Ignoring addr: " + addr + " of interface " + ni.getDisplayName());
                    }
                }
            }
        } catch (SocketException ex) {
            System.out.println("Error: " + ex);
        }
        return result;
    }

    public static void main(String[] args) throws Exception {

        InetAddress group = InetAddress.getByName(Variables.MULTICAST_ADDRESS);
        String msg = "This is a test message sent by MULTICAST";
        System.out.println("\nSending by multicast the message: " + msg);
        MulticastSocket ms = new MulticastSocket();
        DatagramPacket dp = new DatagramPacket(msg.getBytes(), msg.length(), group, Variables.MULTICAST_PORT);

        List<InetAddress> addrs = obtainValidAddresses(group);
        for (InetAddress addr: addrs) {
            System.out.println("Sending on " + addr);
            ms.setInterface(addr);
            ms.send(dp);
        }
        ms.close();
    }

}
