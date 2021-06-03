package dtn;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

/**
 * Modulação das mensagens a circular na rede dtn
 */
public class Message implements Serializable {
    private final String id;
    private final List<InetAddress> path;
    private int ttl;
    private final int type;
    private final FileNDN file;


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

    public void decrementTtl() {
        this.ttl -= 1;
    }
}
