package dtn;


import java.io.IOException;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DTN {
    private static DTN instance = null;
    private final String name;
    private final Cache cache;
    private final Map<String, Message> pendingMessages;

    private DTN(String name) {
        this.name = name;
        this.cache = new Cache();
        this.pendingMessages = new HashMap<>();
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
        FileNDN file = new FileNDN(fileName, new byte[0]);
        Message interest = new Message(messageID, new ArrayList<>(), Constants.TTL, Constants.INTEREST, file);
        for (InetAddress dest : destination) {
            try {
                System.out.println("Enviei um pedido!");
                interest.send(dest);
            } catch (IOException e) {
                System.out.println("Can't send NDN interest to " + dest + "!");
            }
        }
    }

    public void receiveInterest(Message message) {
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
            System.out.println("Ia enviar");
            //sendInterest();
            //TODO
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
