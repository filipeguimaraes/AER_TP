package dtn;


import p2p.P2P;

import java.io.IOException;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class DTN {
    private static DTN instance = null;
    private final Cache cache;
    private final String name;
    private final Deque<Message> postPendent = new ArrayDeque<>();
    private final List<String> interestsSent = new ArrayList<>();

    private DTN(String name) {
        this.name = name;
        this.cache = new Cache();
        Receiver.receiveMulticast();
    }

    public static DTN getInstance() {
        if (instance.name == null) {
            System.out.println("Something went wrong on the DTN!");
        }
        return instance;
    }

    public static DTN getInstance(String name) {
        if (instance == null) {
            instance = new DTN(name);
        }
        return instance;
    }

    public void sendInterest(String fileName, List<InetAddress> destination) {
        String messageID = this.name + fileName + LocalDateTime.now();
        interestsSent.add(messageID);
        FileNDN file = new FileNDN(fileName, new byte[0]);
        for (InetAddress dest : destination) {
            try {
                System.out.println("Enviei um pedido!");
                Message interest = new Message(messageID,
                        new ArrayList<>(),
                        Constants.TTL,
                        Constants.INTEREST,
                        file);
                interest.send(dest);
            } catch (IOException e) {
                System.out.println("Can't send NDN interest to " + dest + "! more info: ");
                e.printStackTrace();
            }
        }
    }

    public void sendInterest(Message message) {
        interestsSent.add(message.getId());
        if (message.getTtl() > 0) {
            List<InetAddress> destination = new ArrayList<>();
            try {
                P2P.getInstance().lock();
                for (String ip : P2P.getInstance().getPeers().keySet()) {
                    if (P2P.getInstance().getPeers().get(ip).isON()) {
                        destination.add(P2P.getInstance().getPeers().get(ip).getAddress());
                    }
                }
            } finally {
                P2P.getInstance().unlock();
            }

            for (InetAddress dest : destination) {
                try {
                    System.out.println("Reencaminhei um pedido!");
                    message.decrementTtl();
                    message.send(dest);
                } catch (IOException e) {
                    System.out.println("Can't send NDN interest to " + dest + "! more info: ");
                    e.printStackTrace();
                }
            }
        } else System.out.println("Message ignored ttl = 0.");

    }


    public void receiveInterest(Message message) {
        if (!interestsSent.contains(message.getId())) {
            if (cache.containsFile(message.getFile())) {
                InetAddress dest = message.getPath().get(message.getPath().size() - 1);
                List<InetAddress> path = message.getPath();
                path.remove(message.getPath().size() - 1);

                Message response = new Message(message.getId(),
                        path,
                        0,
                        Constants.POST,
                        cache.getFiles().get(message.getFile().getName()));

                sendPost(response);
            } else {
                System.out.println("Recebi um pedido! ttl =" + message.getTtl());
                sendInterest(message);
            }
        }
    }

    public void sendPost(Message message) {
        //TODO
    }

    public void receivePost(Message message) {
        //TODO
    }

    public void downloadFile(Message message) {
        //TODO
    }

    public Cache getCache() {
        return cache;
    }
}
