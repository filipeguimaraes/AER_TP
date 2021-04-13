import java.net.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class Network {
    private static Network instance = null;
    private int helloTime = Variables.HELLO_TIME;
    private int deadTime = Variables.DEAD_TIME;
    private Map<String, Peer> peers;


    private Network() {
        this.peers = new TreeMap<>();
        sendHellos();
        receiveMulticast();
        killPeers();
    }

    public static Network getInstance() {
        if (instance == null) {
            instance = new Network();
        }
        return instance;
    }

    /**
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
            Enumeration<InetAddress> addresses = ni.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress addr = addresses.nextElement();
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
                    DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
                    ms.receive(dp);
                    Multiplexer.receive(dp);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }).start();
    }

    public void sendHellos() {
        new Thread(() -> {
            while (true) {
                try {
                    List<InetAddress> addrs = obtainValidAddresses(InetAddress.getByName(Variables.MULTICAST_ADDRESS));
                    String msg = "HELLO";
                    //System.out.println("\nSending the message: " + msg);
                    MulticastSocket ms = new MulticastSocket();
                    DatagramPacket dp = new DatagramPacket(
                            msg.getBytes(),
                            msg.length(),
                            InetAddress.getByName(Variables.MULTICAST_ADDRESS),
                            Variables.MULTICAST_PORT);

                    for (InetAddress addr : addrs) {
                        //System.out.println("Sending on " + addr);
                        ms.setInterface(addr);
                        ms.send(dp);

                    }
                    ms.close();
                    Thread.sleep(helloTime);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }).start();

    }

    public void addPeer(Peer peer) {
        if (!peers.containsKey(peer.getAddress().toString())) {
            peers.put(peer.getAddress().toString(), peer);
            System.out.println("Add peer: " + peer.getAddress().toString());
        } else {
            peers.remove(peer.getAddress().toString());
            peers.put(peer.getAddress().toString(), peer);
        }
    }

    public void killPeers() {
        new Thread(() -> {
            while (true) {
                for (Peer peer : peers.values()) {
                    Duration duration = Duration.between(peer.getAddDate(), LocalDateTime.now());
                    if (duration.toMillis() > deadTime) {
                        System.out.println("Peer desconectado: " + peer.getAddress().toString());
                        peers.remove(peer.getAddress().toString());
                    }
                }
                try {
                    Thread.sleep(deadTime);
                } catch (Exception ignore) {
                }

            }
        }).start();
    }

    public void setHelloTime(int helloTime) {
        this.helloTime = helloTime;
    }

    public void setDeadTime(int deadTime) {
        this.deadTime = deadTime;
    }
}
