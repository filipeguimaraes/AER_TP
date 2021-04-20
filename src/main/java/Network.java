import java.io.*;
import java.net.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Network {
    private static Network instance = null;
    private int helloTime = Variables.HELLO_TIME;
    private int deadTime = Variables.DEAD_TIME;
    private final ReentrantLock lock = new ReentrantLock();
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



                while (true) {
                    byte[] buffer = new byte[8192];
                    DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
                    ms.receive(dp);

                    ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
                    ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(bais));
                    Message message = (Message) ois.readObject();

                    Multiplexer.receive(message,dp.getAddress());
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

                    Message hello = new Message(Variables.HELLO,null,null);
                    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    final ObjectOutputStream oos = new ObjectOutputStream(baos);
                    oos.writeObject(hello);

                    final byte[] data = baos.toByteArray();
                    MulticastSocket ms = new MulticastSocket();
                    DatagramPacket dp = new DatagramPacket(
                            data,
                            data.length,
                            InetAddress.getByName(Variables.MULTICAST_ADDRESS),
                            Variables.MULTICAST_PORT);

                    for (InetAddress addr : addrs) {
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
        try {
            lock();
            if (!peers.containsKey(peer.getAddress().toString())) {
                peers.put(peer.getAddress().toString(), peer);
                System.out.println("("+LocalDateTime.now()+") Add peer: " + peer.getAddress().toString());
            } else {
                peers.remove(peer.getAddress().toString());
                peers.put(peer.getAddress().toString(), peer);
            }
        }finally {
            unlock();
        }
    }

    public void killPeers() {
        new Thread(() -> {
            while (true) {
                try {
                    lock();
                    ArrayList<Peer> peers = new ArrayList<>(this.peers.values());
                    for (Peer peer : peers) {
                        Duration duration = Duration.between(peer.getAddDate(), LocalDateTime.now());
                        if (duration.toMillis() > deadTime) {
                            System.out.println("("+LocalDateTime.now()+") Peer desconectado: " + peer.getAddress().toString());
                            this.peers.remove(peer.getAddress().toString());
                        }
                    }
                }finally {
                    unlock();
                }
                try {
                    Thread.sleep(helloTime);
                } catch (Exception ignore) {}

            }
        }).start();
    }

    public void sendSimpleMessage(int type,String message,InetAddress address) throws IOException {

        Message query = new Message(type,message,null);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(query);

        final byte[] data = baos.toByteArray();
        DatagramSocket ds = new DatagramSocket();

        DatagramPacket dp = new DatagramPacket(
                    data,
                    data.length,
                    address,
                    Variables.MULTICAST_PORT);
        ds.send(dp);

        ds.close();
    }

    public void loadPeersFromConfig(String path) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(path));
        JSONArray jsonArray = (JSONArray) jsonObject.get("nodes");

        for (String s : (Iterable<String>) jsonArray) {
            Message m = new Message(Variables.ACK,null,null);
            System.out.println(s);
        }

    }


    public void lock(){
        this.lock.lock();
    }

    public void unlock(){
        this.lock.unlock();
    }

    public void setHelloTime(int helloTime) {
        this.helloTime = helloTime;
    }

    public void setDeadTime(int deadTime) {
        this.deadTime = deadTime;
    }
}
