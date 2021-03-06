package p2p;

import dtn.DTN;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Modulo principal da rede p2p. Só existe uma instância por nó.
 */
public class P2P {
    private static P2P instance = null;
    private final ReentrantLock lock = new ReentrantLock();
    private int helloTime = Constantes.HELLO_TIME;
    private int deadTime = Constantes.DEAD_TIME;
    private Map<String, Peer> peers;


    private P2P() {
        this.peers = new TreeMap<>();
    }

    /**
     * Obter a instância única da rede p2p.
     *
     * @return Instância única da rede.
     */
    public static P2P getInstance() {
        if (instance == null) {
            instance = new P2P();
        }
        return instance;
    }

    /**
     * Adiciona um peer á lista de peers atualizando o timestamp.
     *
     * @param peer P2P.Peer a ser adicionado.
     */
    public void addPeer(Peer peer) {
        try {
            lock();
            if (!peers.containsKey(peer.getAddress().toString())) {
                peer.activate();
                peers.put(peer.getAddress().toString(), peer);
            } else {
                peers.get(peer.getAddress().toString()).activate();
            }

        } finally {
            unlock();
        }
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
                Constantes.MULTICAST_PORT);
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

    public void sendSearch(String fileName) {
        try {
            lock();
            List<InetAddress> peersConhecidos = new ArrayList<>();
            for (String ip : peers.keySet()) {
                if (peers.get(ip).isON()) {
                    peersConhecidos.add(peers.get(ip).getAddress());
                }
            }
            DTN.getInstance().sendInterest(fileName, peersConhecidos);
        } finally {
            unlock();
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
                Message hello = new Message(Constantes.HELLO, null);
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

        List<File> files = new ArrayList<>();
        for (String s : (Iterable<String>) jsonArray) {
            File file = new File(s);
            files.add(file);
        }

        DTN.getInstance().getCache().addFiles(files);
    }

    /**
     * Imprime no ecrã os ficheiros que ele tem em cache.
     */
    public void printFilesKnown() {
        try {
            DTN.getInstance().getCache().lock();
            List<String> files = new ArrayList<>(DTN.getInstance().getCache().getFiles().keySet());
            int i = 1;
            System.out.println("---------------------------------------");
            for (String file : files) {
                System.out.println(i + ": [" + file + "]");
                i++;
            }
            if (i == 1) {
                System.out.println("No files.");
            }
            System.out.println("---------------------------------------");
        } finally {
            DTN.getInstance().getCache().unlock();
        }

    }


    /**
     * Retorna a lista de peers no momento. (Not Thread safe)
     *
     * @return Lista de peers
     */
    public Map<String, Peer> getPeers() {
        return peers;
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
