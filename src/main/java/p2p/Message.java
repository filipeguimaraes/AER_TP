package p2p;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class Message implements Serializable {

    int type;
    String message;


    public Message(int type, String message) {
        this.type = type;
        this.message = message;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Usado caso seja uma mensagem de troca de peers para separar os diferentes peers
     * na mensagem. (não usado)
     * @return Lista de peers.
     * @throws Exception Caso este método seja usado num tipo de mensagem inválido.
     */
    public List<InetAddress> getPeers() throws Exception {
        if(!(this.type == Constantes.PEERS))
            throw new Exception("Tipo de mensagem inválido!");

        List<InetAddress> peers = new ArrayList<>();

        for (String s : message.split(";")) {
            peers.add(InetAddress.getByName(s));
        }

        return peers;
    }
}
