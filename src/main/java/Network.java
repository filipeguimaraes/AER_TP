import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.image.AreaAveragingScaleFilter;
import java.io.*;
import java.net.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class Network {
    private static Network instance = null;
    private final ReentrantLock lock = new ReentrantLock();
    private int helloTime = Variables.HELLO_TIME;
    private int deadTime = Variables.DEAD_TIME;
    private Map<String, Peer> peers;
    private Map<String, File> files;

    private Map<String, List<Peer>> filePeers;


    private Network() {
        this.peers = new TreeMap<>();
        this.files = new HashMap<>();
        this.filePeers = new HashMap<>();
        obtainPeersOnMulticast();
        receiveMulticast();
        killPeers();
        sendPings();
    }

    /**
     * Obter a instância única da rede p2p.
     *
     * @return Instância única da rede.
     */
    public static Network getInstance() {
        if (instance == null) {
            instance = new Network();
        }
        return instance;
    }

    /**
     * Obter os endereços multicast no nodo local.
     *
     * @param group MULTICAST ADDRESS
     * @return Lista de endereços na rede
     */
    public List<InetAddress> obtainValidAddresses(InetAddress group) throws SocketException {
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


    /**
     * Serviço para estar à escuta de mensagens.
     */
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

                    Multiplexer.receive(message, dp.getAddress());
                }
            } catch (Exception e) {
                System.out.println("Receive Multicast:" + e.getMessage());
            }

        }).start();
    }


    /**
     * Serviço que obtém peers diretamente conectados usando o multicast.
     */
    public void obtainPeersOnMulticast() {
        new Thread(() -> {
            while (true) {
                try {
                    List<InetAddress> addrs = obtainValidAddresses(InetAddress.getByName(Variables.MULTICAST_ADDRESS));

                    Message hello = new Message(Variables.HELLO, null);
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
                    Thread.sleep(Variables.HELLO_TIME);
                } catch (Exception e) {
                    System.out.println("obtainPeersOnMulticast: " + e.getMessage());
                }
            }
        }).start();

    }

    /**
     * Adiciona um peer á lista de peers atualizando o timestamp.
     *
     * @param peer Peer a ser adicionado.
     */
    public void addPeer(Peer peer) {
        try {
            lock();
            if (!peers.containsKey(peer.getAddress().toString())) {
                peer.activate();
                peers.put(peer.getAddress().toString(), peer);
                System.out.println("(" + LocalDateTime.now() + ") Add peer: " + peer.getAddress().toString());
            } else {
                peers.get(peer.getAddress().toString()).activate();
            }

        } finally {
            unlock();
        }
    }

    /**
     * Serviço para regularmente pesquisar por peers desconectados, ou seja,
     * com um timestamp mais antigo do que o predefinido.
     */
    public void killPeers() {
        new Thread(() -> {
            while (true) {
                try {
                    lock();
                    ArrayList<Peer> peers = new ArrayList<>(this.peers.values());
                    for (Peer peer : peers) {
                        if (peer.isON()) {
                            Duration duration = Duration.between(peer.getTimeStamp(), LocalDateTime.now());
                            if (duration.toMillis() > deadTime) {
                                System.out.println("(" + LocalDateTime.now() +
                                        ") Peer desconectado: " + peer.getAddress().toString());
                                this.peers.get(peer.getAddress().toString()).deactivate();
                            }
                        }
                    }
                } finally {
                    unlock();
                }
                try {
                    Thread.sleep(helloTime);
                } catch (Exception e) {
                    System.out.println("killPeers: " + e.getMessage());
                }

            }
        }).start();
    }

    /**
     * Método para enviar uma mensagem ao endereço especificado.
     *
     * @param message Objeto mensagem a enviar.
     * @param address Endereço de destino.
     * @throws IOException Caso haja problema a enviar a mensagem.
     */
    public void sendSimpleMessage(Message message, InetAddress address) throws IOException {

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(message);

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



    /**
     * Adiciona aos peers conhecidos a lista passada em argumento.
     *
     * @param peers Lista de peers a adicionar.
     */
    public void addPeers(List<InetAddress> peers) {
        for (InetAddress peer : peers) {
            Peer newPeer = new Peer(peer, LocalDateTime.now());
            addPeer(newPeer);
        }

    }

    /**
     * Função para enviar mensagens para verificar se algum peer se desconectou.
     */
    public void sendPings() {
        new Thread(() -> {
            while (true) {
                try {
                    lock();
                    List<Peer> peers = new ArrayList<>(this.peers.values());
                    Message ping = new Message(Variables.PING, null);
                    for (Peer p : peers) {
                        try {
                            sendSimpleMessage(ping, p.getAddress());
                        } catch (IOException e) {
                            this.peers.get(p.getAddress().toString()).deactivate();
                            //System.out.println("Não foi possível enviar o ping para " + p.getAddress() + ".");
                        }
                    }
                } finally {
                    unlock();
                }
                try {
                    Thread.sleep(Variables.HELLO_TIME);
                } catch (Exception e) {
                    System.out.println("sendPings: " + e.getMessage());
                }
            }
        }).start();

    }

    public void printConnectedPeers() {
        try {
            lock();
            List<Peer> peers = new ArrayList<>(this.peers.values());
            int i = 1;
            System.out.println("-----------------------------------");
            for (Peer p : peers) {
                if (p.isON()) {

                    System.out.println(i + ") Address: " +
                            p.getAddress() +
                            ", Add in: " +
                            p.getAddDate() +
                            ", Time on: " +
                            Duration.between(p.getAddDate(), LocalDateTime.now()).toMinutes() +
                            " min.");
                    i++;
                }
            }
            if (i == 1) {
                System.out.println("Nenhum peer conectado!");
            }
            System.out.println("-----------------------------------");

        } finally {
            unlock();
        }
    }

    public void printDisconnectedPeers() {
        try {
            lock();
            List<Peer> peers = new ArrayList<>(this.peers.values());
            int i = 1;
            System.out.println("-----------------------------------");
            for (Peer p : peers) {
                if (!p.isON()) {

                    System.out.println(i + ") Address: " +
                            p.getAddress() +
                            ", Add in: " +
                            p.getAddDate() +
                            ", Time off: " +
                            Duration.between(p.getTimeStamp(), LocalDateTime.now()).toMinutes() +
                            " min.");
                    i++;
                }
            }
            if (i == 1) {
                System.out.println("Nenhum peer desconectado!");
            }
            System.out.println("-----------------------------------");

        } finally {
            unlock();
        }
    }

    public void sendSearch(String query) {
        System.out.println("Searching for " + query + "...");
        Message search = new Message(Variables.QUERY, query);
        try {
            lock();
            List<Peer> peers = new ArrayList<>(this.peers.values());
            for (Peer p : peers) {
                if (p.isON()) {
                    try {
                        sendSimpleMessage(search, p.getAddress());
                    } catch (IOException e) {
                        System.out.println("Cannot send query to " + p.getAddress());
                    }
                }
            }
        } finally {
            unlock();
        }
    }

    public void searchFile(String file, InetAddress requestOrigin) {
        System.out.println("Recebi " + file);
        List<String> keys = new ArrayList<>(this.files.keySet());
        for (String key : keys) {
            if (key.contains(file)) {
                try {
                    //Caso tenha registo do ficheiro envia a key correspondente!
                    sendSimpleMessage(new Message(Variables.QUERY_RESPONSE, file ), requestOrigin);
                } catch (IOException e) {
                    System.out.println("Cannot response to " + requestOrigin.toString());
                }
                break;
            }
        }

    }

    /**
     * Carrega peers manualmente especificados no ficheiro de configuração.
     *
     * @param path Caminho para o ficheiro de configuração.
     * @throws IOException    Caso não seja possível carregar o ficheiro.
     * @throws ParseException Caso haja problemas em processar o JSON.
     */
    public void loadPeersFromConfig(String path) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(path));
        JSONArray jsonArray = (JSONArray) jsonObject.get("nodes");

        for (String s : (Iterable<String>) jsonArray) {
            try {
                Peer newPeer = new Peer(InetAddress.getByName(s), LocalDateTime.now());
                Message hello = new Message(Variables.HELLO, null);
                sendSimpleMessage(hello, newPeer.getAddress());
                addPeer(newPeer);
            } catch (IOException e) {
                System.out.println("Cannot connect to address: " + s);
            }
        }
    }

    /**
     * Carrega ficheiros manualmente especificados no ficheiro de configuração.
     *
     * @param path Caminho para o ficheiro de configuração.
     * @throws IOException    Caso não seja possível carregar o ficheiro.
     * @throws ParseException Caso haja problemas em processar o JSON.
     */
    public void loadFilesFromConfig(String path) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(path));
        JSONArray jsonArray = (JSONArray) jsonObject.get("files");

        for (String s : (Iterable<String>) jsonArray) {
            files.put(s, null);
        }
    }

    public void sourcePeerFile(String file, InetAddress peer){
        if (filePeers.containsKey(file)){
            System.out.println("Add peer as file("+file+"): "+peer.toString());
            filePeers.get(file).add(peers.get(peer.toString()));
        } else {
            System.out.println("First peer with file(\"+file+\"): "+peer);
            List<Peer> filep = new ArrayList<>();
            filep.add(peers.get(peer.toString()));
            filePeers.put(file,filep);
        }
    }

    //*************************************************//
    // Métodos de controlo de acesso à lista de peers. //
    //*************************************************//
    public void lock() {
        this.lock.lock();
    }

    public void unlock() {
        this.lock.unlock();
    }

}
