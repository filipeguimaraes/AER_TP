package dtn;


import p2p.P2P;

import java.io.IOException;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class DTN {
    private static DTN instance = null;
    private final Cache cache;
    private final String name;
    private final Map<String, Message> postPendent = new HashMap<>();
    private final List<String> postsReceived = new ArrayList<>();
    private final List<String> interestsSent = new ArrayList<>();
    private ReentrantLock lock = new ReentrantLock();

    private DTN(String name) {
        this.name = name;
        this.cache = new Cache();
        Receiver.receiveMulticast();
        retransmit();
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
                Message interest = new Message(messageID,
                        new ArrayList<>(),
                        Constants.TTL,
                        Constants.INTEREST,
                        file);
                interest.send(dest);

                System.out.println("Enviei um pedido para: " + dest.getHostAddress());
            } catch (IOException e) {
                System.out.println("Can't send NDN interest to " + dest + "! more info: ");
                e.printStackTrace();
            }
        }
    }

    public void sendInterest(Message message) {
        interestsSent.add(message.getId());
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
        message.decrementTtl();
        for (InetAddress dest : destination) {
            try {
                message.send(dest);
                System.out.println("Reencaminhei um pedido para: " + dest.getHostAddress());
            } catch (IOException e) {
                System.out.println("Can't send NDN interest to " + dest + "! more info: ");
                e.printStackTrace();
            }
        }

    }


    public void receiveInterest(Message message) {
        if (cache.containsFile(message.getFile())) {
            List<InetAddress> path = message.getPath();
            Message response = new Message(message.getId(),
                    path,
                    0,
                    Constants.POST,
                    cache.getFiles().get(message.getFile().getName()));


            System.out.println("I have the file!");
            System.out.println("Waiting 3 seconds for testing dtn!");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                System.out.println("Can't sleep anymore!");
            }
            postPendent.put(response.getId(), response);
            sendPost(response);
        } else {
            if (!interestsSent.contains(message.getId())) {
                if (message.getTtl() > 0) {
                    sendInterest(message);
                } else System.out.println("TTL is 0.");
            }
        }

    }

    public void confirmPost(String messageID) {
        postPendent.remove(messageID);
    }

    //tambem reencaminhar post
    public void sendPost(Message message) {
        try {
            System.out.println("Send a post!" + message.getPath());
            message.send(message.getPath().get(message.getPath().size() - 1));
        } catch (IOException e) {
            System.out.println("Cannot send Post! Retrying later.");
        }
    }

    public void receivePost(Message message) {
        System.out.println("Receive a post! " + message.getPath());
        if (message.getPath().size() == 0) {
            if (postsReceived.contains(message.getId())) {
                System.out.println("Post ignored!");
            } else {
                cache.addFile(message.getFile());
                try {
                    message.getFile().saveFile();
                } catch (IOException e) {
                    System.out.println("Error downloading file!");
                }
            }
        } else {
            postPendent.put(message.getId(), message);
            sendPost(message);
        }
    }

    public void retransmit() {
        new Thread(() -> {
            while (true) {
                try {
                    lock.lock();
                    for (Message m : postPendent.values()) {
                        try {
                            System.out.println("Trying to retransmit to " + m.getPath().get(m.getPath().size() - 1));
                            m.send(m.getPath().get(m.getPath().size() - 1));
                        } catch (IOException e) {
                            System.out.println("Cannot retransmit! Trying later.");
                        }
                    }
                } finally {
                    lock.unlock();
                }
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    System.out.println("Can't sleep anymore!");
                }
            }
        }).start();
    }


    public Cache getCache() {
        return cache;
    }
}
