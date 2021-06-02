package dtn;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

public class Message {
    private String id;
    private List<InetAddress> path; //o primeiro é a origem
    private int ttl = Constants.TTL;
    private int type;
    private FileNDN file;



    public Message(String id, List<InetAddress> path, int ttl, int type, FileNDN file) {
        this.id = id;
        this.path = path;
        this.ttl = ttl;
        this.type = type;
        this.file = file;
    }

    /**
     * Método para enviar uma mensagem ao endereço especificado.
     *
     * @param address Endereço de destino.
     * @throws IOException Caso haja problema a enviar a mensagem.
     */
    public void send(InetAddress address) throws IOException {

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(this);

        final byte[] data = baos.toByteArray();
        DatagramSocket ds = new DatagramSocket();

        DatagramPacket dp = new DatagramPacket(
                data,
                data.length,
                address,
                Constants.MULTICAST_PORT);
        ds.send(dp);

        ds.close();
    }


    public String getId() {
        return id;
    }

    public List<InetAddress> getPath() {
        return path;
    }

    public int getTtl() {
        return ttl;
    }

    public int getType() {
        return type;
    }

    public FileNDN getFile() {
        return file;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPath(List<InetAddress> path) {
        this.path = path;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setFile(FileNDN file) {
        this.file = file;
    }
}
